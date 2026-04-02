from fastapi import FastAPI, HTTPException
from fastapi.middleware.cors import CORSMiddleware
from fastapi.staticfiles import StaticFiles
from pydantic import BaseModel
from openai import OpenAI
import os
import requests

app = FastAPI()

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

client = OpenAI(
    api_key='sk-b80fc94b1e7244b1a283c5c4924ea1d4',
    base_url="https://api.deepseek.com"
)

def get_stock_history(ticker):
    """Call Java API to get stock history"""
    base_url = "http://localhost:8080/api/portfolio"
    url = f"{base_url}/{ticker.upper()}/history"
    try:
        response = requests.get(url, timeout=5)
        if response.status_code == 200:
            return response.json()
        return None
    except Exception as e:
        print(f"Error calling stock history API: {str(e)}")
        return None

class ChatRequest(BaseModel):
    message: str

class InvestmentRequest(BaseModel):
    news: list[str]
    holdings: list[dict]

@app.post("/api/chat")
async def chat(request: ChatRequest):
    try:
        # Define tools for the LLM to use
        tools = [
            {
                "type": "function",
                "function": {
                    "name": "get_stock_history",
                    "description": "Get historical stock data for a specific ticker",
                    "parameters": {
                        "type": "object",
                        "properties": {
                            "ticker": {
                                "type": "string",
                                "description": "Stock ticker symbol (e.g., AAPL, MSFT)"
                            }
                        },
                        "required": ["ticker"]
                    }
                }
            }
        ]

        # First LLM call to analyze the request
        response = client.chat.completions.create(
            model="deepseek-chat",
            messages=[
                {"role": "system", "content": "You are a professional financial investment assistant. Please answer the user's questions in English. When asked about stock history, use the get_stock_history tool to retrieve data."},
                {"role": "user", "content": request.message},
            ],
            tools=tools,
            tool_choice="auto",
            stream=False
        )

        # Check if LLM wants to call a tool
        if response.choices[0].message.tool_calls:
            tool_calls = response.choices[0].message.tool_calls
            tool_call = tool_calls[0]
            
            if tool_call.function.name == "get_stock_history":
                # Extract ticker from tool call arguments
                ticker = tool_call.function.arguments
                ticker = eval(ticker).get("ticker")
                
                # Call the Java API
                stock_history = get_stock_history(ticker)
                
                # Prepare tool response
                tool_response = {
                    "tool_call_id": tool_call.id,
                    "role": "tool",
                    "name": "get_stock_history",
                    "content": str(stock_history)
                }
                
                # Second LLM call to summarize the results
                second_response = client.chat.completions.create(
                    model="deepseek-chat",
                    messages=[
                        {"role": "system", "content": "You are a professional financial investment assistant. Please answer the user's questions in English. When asked about stock history, use the get_stock_history tool to retrieve data."},
                        {"role": "user", "content": request.message},
                        response.choices[0].message,
                        tool_response
                    ],
                    stream=False
                )
                
                return {"response": second_response.choices[0].message.content}
        
        # If no tool call is needed, return the direct response
        return {"response": response.choices[0].message.content}
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

@app.post("/api/investment-advice")
async def investment_advice(request: InvestmentRequest):
    try:
        holdings_str = "\n".join([f"- {item['stock']}: {item['quantity']} shares" for item in request.holdings])
        news_str = "\n".join([f"{i+1}. {news}" for i, news in enumerate(request.news)])
        
        prompt = f"""
        Please act as a professional financial investment advisor, analyze the following news and user holdings, and provide investment recommendations.

        User's Current Holdings:
        {holdings_str}

        Today's News:
        {news_str}

        Please categorize the news into two categories:
        1. News related to the user's current holdings - provide specific buy/sell recommendations
        2. News not related to the user's current holdings - provide potential investment opportunity suggestions

        Please respond in English with clear structure and specific recommendations.
        """

        response = client.chat.completions.create(
            model="deepseek-chat",
            messages=[
                {"role": "system", "content": "You are a professional financial investment advisor who analyzes news and holdings to provide investment recommendations."},
                {"role": "user", "content": prompt},
            ],
            stream=False
        )
        return {"advice": response.choices[0].message.content}
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

if __name__ == "__main__":
    import uvicorn
    
    uvicorn.run(app, host="0.0.0.0", port=8000)
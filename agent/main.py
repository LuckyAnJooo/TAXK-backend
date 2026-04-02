from fastapi import FastAPI, HTTPException
from fastapi.middleware.cors import CORSMiddleware
from pydantic import BaseModel
from openai import OpenAI
import requests
import json

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

JAVA_BASE = "http://localhost:8080/api"

FINANCE_SYSTEM = (
    "You are a professional financial investment assistant. "
    "You ONLY handle questions related to finance, stocks, investments, and portfolio management. "
    "If the user asks about anything unrelated to finance (e.g. weather, sports, cooking), "
    "you must refuse and reply exactly: "
    "'I am a financial assistant and can only help with finance-related questions such as "
    "stock analysis, portfolio management, and investment advice. "
    "Please ask me something related to finance.'"
)

# --- Tool definitions ---

PORTFOLIO_TOOL = {
    "type": "function",
    "function": {
        "name": "get_portfolio",
        "description": (
            "Retrieve the user's current portfolio: all holdings with quantities, "
            "average cost, current price, market value, P&L, day change, and portfolio weight."
        ),
        "parameters": {"type": "object", "properties": {}, "required": []}
    }
}

HISTORY_TOOL = {
    "type": "function",
    "function": {
        "name": "get_stock_history",
        "description": "Get historical daily price data for a specific stock ticker held in the user's portfolio.",
        "parameters": {
            "type": "object",
            "properties": {
                "ticker": {
                    "type": "string",
                    "description": "Stock ticker symbol, e.g. AAPL, TSLA, NVDA"
                }
            },
            "required": ["ticker"]
        }
    }
}

NEWS_TOOL = {
    "type": "function",
    "function": {
        "name": "get_news",
        "description": "Get current general market news headlines to understand recent market trends.",
        "parameters": {"type": "object", "properties": {}, "required": []}
    }
}

ANALYSIS_TOOLS = [PORTFOLIO_TOOL, HISTORY_TOOL]
RECOMMEND_TOOLS = [PORTFOLIO_TOOL, HISTORY_TOOL, NEWS_TOOL]


# --- Tool execution ---

def call_tool(name: str, args: dict) -> dict:
    try:
        if name == "get_portfolio":
            # /api/portfolio makes N sequential Finnhub calls (one per holding),
            # so allow up to 60 s for portfolios with many positions.
            r = requests.get(f"{JAVA_BASE}/portfolio", timeout=60)
            return r.json() if r.status_code == 200 else {"error": f"HTTP {r.status_code}"}
        elif name == "get_stock_history":
            ticker = args.get("ticker", "").upper()
            r = requests.get(f"{JAVA_BASE}/portfolio/{ticker}/history", timeout=30)
            return r.json() if r.status_code == 200 else {"error": f"HTTP {r.status_code}"}
        elif name == "get_news":
            r = requests.get(f"{JAVA_BASE}/portfolio/news", timeout=15)
            return r.json() if r.status_code == 200 else {"error": f"HTTP {r.status_code}"}
        else:
            return {"error": f"Unknown tool: {name}"}
    except Exception as e:
        return {"error": str(e)}


# --- Agentic loop ---

def run_agent(messages: list, tools: list, response_format: dict = None, max_rounds: int = 10) -> str:
    """
    Run the agentic loop: keep calling the LLM and executing tool calls
    until the model produces a final text response (no more tool calls).
    """
    for _ in range(max_rounds):
        kwargs = {
            "model": "deepseek-chat",
            "messages": messages,
            "tools": tools,
            "tool_choice": "auto",
            "stream": False,
        }
        if response_format:
            kwargs["response_format"] = response_format

        response = client.chat.completions.create(**kwargs)
        msg = response.choices[0].message

        if not msg.tool_calls:
            return msg.content

        # Append the assistant turn (with tool_calls)
        messages.append(msg)

        # Execute every tool call in this round and append results
        for tc in msg.tool_calls:
            args = json.loads(tc.function.arguments)
            result = call_tool(tc.function.name, args)
            messages.append({
                "role": "tool",
                "tool_call_id": tc.id,
                "name": tc.function.name,
                "content": json.dumps(result, ensure_ascii=False),
            })

    raise RuntimeError("Agent exceeded maximum tool-call rounds")


# --- Endpoints ---

class ChatRequest(BaseModel):
    message: str


@app.post("/api/chat")
async def chat(request: ChatRequest):
    """
    Portfolio analysis chat.
    The AI will call get_portfolio and get_stock_history as needed,
    then give a buy/sell/hold recommendation based on the user's full context.
    """
    try:
        messages = [
            {"role": "system", "content": FINANCE_SYSTEM},
            {"role": "user", "content": request.message},
        ]
        reply = run_agent(messages, ANALYSIS_TOOLS)
        return {"response": reply}
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))


@app.post("/api/recommend")
async def recommend():
    """
    AI stock recommendation.
    The AI gathers the user's portfolio, stock histories, and market news,
    then returns a structured list of recommended tickers with reasons.
    Response: {"tickers": ["AAPL", "TSLA"], "reasons": {"AAPL": "...", "TSLA": "..."}}
    """
    try:
        system = (
            FINANCE_SYSTEM + "\n\n"
            "After gathering context with tools, respond ONLY with a JSON object — no markdown, no explanation outside the JSON. "
            "The JSON must have exactly two keys:\n"
            '  "tickers": an array of recommended ticker strings (e.g. ["AAPL", "TSLA"])\n'
            '  "reasons": an object mapping each ticker to a concise reason string\n'
            "Example: {\"tickers\": [\"AAPL\"], \"reasons\": {\"AAPL\": \"Strong earnings growth...\"}}"
        )
        messages = [
            {"role": "system", "content": system},
            {
                "role": "user",
                "content": (
                    "Analyze my current portfolio and today's market news, "
                    "then recommend stocks I should consider. "
                    "Return your answer as a JSON object with 'tickers' and 'reasons'."
                ),
            },
        ]
        raw = run_agent(messages, RECOMMEND_TOOLS, response_format={"type": "json_object"})
        result = json.loads(raw)
        return result
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))


if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=8000, timeout_keep_alive=120)

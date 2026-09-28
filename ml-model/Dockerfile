FROM python:3.13-slim

WORKDIR /app

COPY requirements.txt .

RUN pip install --no-cache-dir -r requirements.txt

COPY app.py .
COPY credit_risk_model.joblib .

EXPOSE 8090

CMD ["python", "-m", "uvicorn", "app:app", "--host", "0.0.0.0", "--port", "8090"]
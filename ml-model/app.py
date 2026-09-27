from pathlib import Path

import joblib
import numpy as np
from fastapi import FastAPI
from pydantic import BaseModel, Field

MODEL_PATH = Path(__file__).parent / "credit_risk_model.joblib"

model = joblib.load(MODEL_PATH)

app = FastAPI(
    title="ModelRisk Credit Risk Model",
    version="1.0.0",
)


class CreditApplication(BaseModel):
    annualIncome: float = Field(gt=0)
    debtToIncome: float = Field(ge=0, le=1)
    creditHistoryYears: float = Field(ge=0, le=60)
    missedPayments: int = Field(ge=0)
    existingLoans: int = Field(ge=0)
    age: int = Field(ge=18, le=100)
    demographicGroup: str = Field(min_length=1, max_length=50)


class PredictionResponse(BaseModel):
    predictedDefault: bool
    defaultProbability: float
    decision: str
    modelVersion: str


@app.get("/health")
def health():
    return {
        "status": "UP",
        "modelLoaded": True,
        "modelVersion": "1.0.0",
    }


@app.post("/predict", response_model=PredictionResponse)
def predict(application: CreditApplication):
    features = np.array(
        [
            [
                application.annualIncome,
                application.debtToIncome,
                application.creditHistoryYears,
                application.missedPayments,
                application.existingLoans,
                application.age,
            ]
        ]
    )

    default_probability = float(
        model.predict_proba(features)[0][1]
    )

    predicted_default = default_probability >= 0.5

    return PredictionResponse(
        predictedDefault=predicted_default,
        defaultProbability=round(default_probability, 6),
        decision="DECLINE" if predicted_default else "APPROVE",
        modelVersion="1.0.0",
    )
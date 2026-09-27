from pathlib import Path

import joblib
import numpy as np
from sklearn.linear_model import LogisticRegression
from sklearn.pipeline import Pipeline
from sklearn.preprocessing import StandardScaler

RANDOM_SEED = 42
SAMPLE_COUNT = 5000

rng = np.random.default_rng(RANDOM_SEED)

annual_income = rng.uniform(15_000, 200_000, SAMPLE_COUNT)
debt_to_income = rng.uniform(0.0, 0.9, SAMPLE_COUNT)
credit_history_years = rng.uniform(0.0, 30.0, SAMPLE_COUNT)
missed_payments = rng.integers(0, 11, SAMPLE_COUNT)
existing_loans = rng.integers(0, 9, SAMPLE_COUNT)
age = rng.integers(18, 76, SAMPLE_COUNT)

features = np.column_stack(
    [
        annual_income,
        debt_to_income,
        credit_history_years,
        missed_payments,
        existing_loans,
        age,
    ]
)

risk_score = (
    (debt_to_income * 3.5)
    + (missed_payments * 0.35)
    + (existing_loans * 0.15)
    - (credit_history_years * 0.06)
    - (annual_income / 200_000)
    + rng.normal(0, 0.45, SAMPLE_COUNT)
)

defaulted = (risk_score > 2.2).astype(int)

pipeline = Pipeline(
    [
        ("scaler", StandardScaler()),
        (
            "classifier",
            LogisticRegression(
                max_iter=1000,
                random_state=RANDOM_SEED,
            ),
        ),
    ]
)

pipeline.fit(features, defaulted)

model_path = Path(__file__).parent / "credit_risk_model.joblib"
joblib.dump(pipeline, model_path)

training_accuracy = pipeline.score(features, defaulted)

print(f"Model saved to: {model_path}")
print(f"Training samples: {SAMPLE_COUNT}")
print(f"Training accuracy: {training_accuracy:.4f}")
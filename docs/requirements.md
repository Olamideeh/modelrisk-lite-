# ModelRisk Requirements

## Purpose

ModelRisk is a global AI-model safety and governance platform for fintech companies.

It evaluates AI systems used for credit scoring, fraud detection, KYC, transaction monitoring, financial recommendations and customer support.

## Evaluation Outcomes

- `PASSED`
- `FAILED`
- `REVIEW_REQUIRED`

## Roles

### MODEL_OWNER

- Registers AI models
- Uploads model versions
- Submits versions for evaluation

### RISK_REVIEWER

- Investigates bias and drift findings
- Approves or rejects model versions

### COMPLIANCE_OFFICER

- Confirms that required regulatory evidence exists

### ADMIN

- Manages platform access and global policies

### SYSTEM

- Executes automated evaluations
- Calculates metrics
- Prevents duplicate evaluations
- Produces findings and evidence

## Model-Version States

- `DRAFT`
- `SUBMITTED`
- `EVALUATING`
- `REVIEW_REQUIRED`
- `APPROVED`
- `REJECTED`
- `DEPLOYED`
- `RETIRED`

A retired model version cannot return to deployment.

## Evaluation Workflow

1. Model owner submits a model version.
2. System sends controlled cases to the AI model.
3. System calculates accuracy, fairness and drift metrics.
4. ModelRisk generates findings and evidence.
5. System returns an evaluation outcome.
6. Risk reviewer investigates review-required findings.
7. Approved model version is released for deployment.

## Core Rules

- All required checks pass → `PASSED`
- Minor accuracy concern → `REVIEW_REQUIRED`
- Critical accuracy failure → `FAILED`
- Minor fairness difference → `REVIEW_REQUIRED`
- Critical fairness violation → `FAILED`
- Significant model drift → `REVIEW_REQUIRED`
- Model endpoint unavailable → `FAILED`
- Required compliance evidence missing → `FAILED`

## Initial AI Model

The first model will be a credit-risk binary classifier built with Python and scikit-learn using synthetic data.

ModelRisk itself will be implemented using Java and Spring Boot.
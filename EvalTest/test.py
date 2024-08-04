from datasets import Dataset
import os
import pandas as pd
import requests
from ragas import evaluate
from ragas.metrics import faithfulness, answer_correctness, context_precision, context_recall, answer_relevancy, \
    context_entity_recall, answer_similarity
from ragas.metrics.critique import harmfulness
questions = [
    'What was nvidia total 2023 revenue',
    'What was Apple 2023 revenue'
]
url = "http://localhost:8080/api/v1/chat"

payload = {"message": questions}
response = requests.post(url, json=payload)
answers = []
for question in questions:
    payload = {"message": question}
    response = requests.post(url, json=payload)
    answer = response.text
    answers.append(answer)
data_samples = {
    'question': questions,
    'answer': answers,
    'contexts': [[
        "Apple's revenue for the year 2022 was $394,328 million"],
        ["Apple's total revenue for 2023 was $383,285 million"]],
    'ground_truth': ["Apple's total revenue for 2023 was $383,285 million",
                     "Apple's revenue for the year 2022 was $394,328 million"]
}

dataset = Dataset.from_dict(data_samples)

score = evaluate(dataset,
                 metrics=[faithfulness, answer_correctness, context_precision, context_recall, answer_relevancy,
                          context_entity_recall, answer_similarity])
df = score.to_pandas()
df.to_csv('score2.csv', index=False)

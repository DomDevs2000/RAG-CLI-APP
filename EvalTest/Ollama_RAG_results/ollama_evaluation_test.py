import os

import pandas as pd
import requests
from datasets import Dataset
from ragas import evaluate
from ragas.metrics import (answer_correctness, answer_relevancy,
                           answer_similarity, context_utilization,
                           context_precision, context_recall, faithfulness)
from ragas.metrics.critique import harmfulness, maliciousness, coherence, correctness, conciseness

df = pd.read_csv('ollama_rag_dataset.csv')

questions = df['question'].tolist()
answers = []
url = 'http://localhost:8080/ollama/v1/chat'
for question in questions:
    payload = {
        'message': question
    }

    response = requests.post(url, json=payload)
    if response.status_code == 200:
        # answers.append(response.json().get('content', 'No answer found'))
        json_response = response.json()

        content = json_response.get('result', {}).get('output', {}).get('content', 'No answer found')

        answers.append(content)
    else:
        answers.append('Error')

contexts = df['contexts'].apply(eval).tolist()
ground_truth = df['ground_truth'].tolist()
data_samples = {
    'question': questions,
    'answer': answers,
    'contexts': contexts,
    'ground_truth': ground_truth
}
dataset = Dataset.from_dict(data_samples)
print(dataset)

score = evaluate(dataset,
                 metrics=[context_precision, context_recall,
                          context_utilization, answer_relevancy, answer_similarity, answer_correctness, faithfulness,
                          harmfulness, maliciousness, coherence, correctness, conciseness])
df = score.to_pandas()
df.to_csv('ollama_rag_metrics.csv', index=False)

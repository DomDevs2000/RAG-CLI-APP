from datasets import Dataset
import os
import pandas as pd
import requests
from ragas import evaluate
from ragas.metrics import faithfulness, answer_correctness, context_precision, context_recall, answer_relevancy, \
    context_entity_recall, answer_similarity
from ragas.metrics.critique import harmfulness
questions =[
    'When was the first super bowl?',
    'Who won the most super bowls?'
]
url = "http://localhost:8080/api/v1/chat"

payload = {"message": questions}
response = requests.post(url, json=payload)
# answer = response.text
answer = "The first superbowl was held on January 15, 1967."
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
                     'The First AFL–NFL World Championship Game was an American football game played on January 15, 1967, at the Los Angeles Memorial Coliseum in Los Angeles,'],
                 ['The Green Bay Packers...Green Bay, Wisconsin.', 'The Packers compete...Football Conference']],
    'ground_truth': ['The first superbowl was held on January 15, 1967',
                     'The New England Patriots have won the Super Bowl a record six times']
}

dataset = Dataset.from_dict(data_samples)

score = evaluate(dataset,
                 metrics=[faithfulness, answer_correctness, context_precision, context_recall, answer_relevancy,
                          context_entity_recall, answer_similarity])
df = score.to_pandas()
df.to_csv('results.csv', index=False)

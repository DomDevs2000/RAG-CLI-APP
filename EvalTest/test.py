from datasets import Dataset 
import os
import json
import requests
from ragas import evaluate
from ragas.metrics import faithfulness, answer_correctness
questions =  'When was the first super bowl?'
url = "http://localhost:8080/api/v1/chat"

payload = {"message": questions}
response = requests.post(url, json=payload)
answer = response.text


data_samples = {
    'question': questions,
    'answer': answer,
    'contexts' : [['The First AFL–NFL World Championship Game was an American football game played on January 15, 1967, at the Los Angeles Memorial Coliseum in Los Angeles,'],
                  ['The Green Bay Packers...Green Bay, Wisconsin.','The Packers compete...Football Conference']],
    'ground_truth': ['The first superbowl was held on January 15, 1967', 'The New England Patriots have won the Super Bowl a record six times']
}

dataset = Dataset.from_dict(data_samples)

score = evaluate(dataset, metrics=[faithfulness, answer_correctness])
df = score.to_pandas()
df.to_csv('score.csv', index=False)

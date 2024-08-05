

import os

import pandas as pd
import requests
from datasets import Dataset
from ragas import evaluate
from ragas.metrics import (answer_correctness, answer_relevancy,
                           answer_similarity, context_entity_recall,
                           context_precision, context_recall, faithfulness)
from ragas.metrics.critique import harmfulness ,maliciousness, coherence, correctness, conciseness

# Read the CSV file
df = pd.read_csv('rag_dataset.csv')

# Extract columns
questions = df['question'].tolist()
answers=[]
url = 'http://localhost:8080/api/v1/chat'  # Your localhost URL

for question in questions:
    # Step 4: Create the payload
    payload = {
        'message': question
    }

    # Step 5: Send the POST request
    response = requests.post(url, json=payload)
    if response.status_code == 200:
        # Add the response content to the answers list
        answers.append(response.json().get('content', 'No answer found'))
    else:
        # Handle the error appropriately
        answers.append('Error')

    # Print the response status and content for each request
    print(f"Status Code: {response.status_code}")
    print(f"Response Content: {response.content.decode('utf-8')}")

    # Print the response status and content for each request
    print(f"Status Code: {response.status_code}")
    print(f"Response Content: {response.content.decode('utf-8')}")
contexts = df['contexts'].apply(eval).tolist()  # Assuming contexts are stored as strings of lists
ground_truth = df['ground_truth'].tolist()
# Store in the required format
data_samples = {
    'question': questions,
    'answer': answers,
    'contexts': contexts,
    'ground_truth': ground_truth
}
dataset = Dataset.from_dict(data_samples)
# Print the result
print(dataset)


score = evaluate(dataset,
                 metrics=[context_precision, context_recall,
                          context_entity_recall, answer_relevancy, answer_similarity, answer_correctness,faithfulness, harmfulness, maliciousness, coherence, correctness, conciseness])
df = score.to_pandas()
df.to_csv('rag_metrics.csv', index=False)

import os

import pandas as pd
from datasets import Dataset
from ragas import evaluate
from ragas.metrics import (context_entity_recall, context_precision,
                           context_recall)

# Load your test set
test_df = pd.read_csv("./test_set.csv")

# test_df = test_df.rename(columns={"ground_truth": "answer"})

# print(test_df)

# Assuming your test_df has 'question', 'context', and 'ground_truth' columns
# Convert your test set DataFrame to the format expected by RAGAS

questions = test_df['question'].tolist()
contexts = test_df['contexts'].apply(eval).tolist()  # Assuming contexts are stored as strings of lists
ground_truth = test_df['ground_truth'].tolist()

# Store in the required format
test_set = {
'question': questions,
    'contexts': contexts,
    'ground_truth': ground_truth
}

hf_dataset = Dataset.from_dict(test_set)

# Run the evaluation
result = evaluate(
    hf_dataset,
    metrics=[context_precision, context_recall, context_entity_recall],
)
df = result.to_pandas()
df.to_csv('evaluation_results.csv', index=False)
# Convert the results to a pandas DataFrame
# results_df = pd.DataFrame(result)
# results_df.head()
#
# # Export the evaluation results to a new CSV
# results_df.to_csv("evaluation_results.csv", index=False)

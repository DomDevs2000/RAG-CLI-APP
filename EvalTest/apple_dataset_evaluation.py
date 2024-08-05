
import pandas as pd
from datasets import Dataset
import os
import pandas as pd
from ragas import evaluate
from ragas.metrics import faithfulness, answer_correctness, context_precision, context_recall, answer_relevancy, \
    context_entity_recall, answer_similarity
from ragas.metrics.critique import harmfulness
# Read the CSV file
df = pd.read_csv('dataset_results.csv')

# Extract columns
questions = df['question'].tolist()
contexts = df['contexts'].apply(eval).tolist()  # Assuming contexts are stored as strings of lists
ground_truth = df['ground_truth'].tolist()

# Store in the required format
data_samples = {
'question': questions,
    'contexts': contexts,
    'ground_truth': ground_truth
}
dataset = Dataset.from_dict(data_samples)
# Print the result
print(dataset)


score = evaluate(dataset,
                 metrics=[context_precision, context_recall,
                          context_entity_recall])
df = score.to_pandas()
df.to_csv('apple_dataset_metrics.csv', index=False)

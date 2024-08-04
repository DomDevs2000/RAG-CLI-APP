from datasets import Dataset
from ragas.metrics import faithfulness, answer_correctness
from ragas import evaluate

data_samples = {
    'question': ['When was the first super bowl?', 'Who won the most super bowls?'],
    'answer': ['The first superbowl was held on Jan 15, 1967',
               'The most super bowls have been won by The New England Patriots'],
    'ground_truth': ['The first superbowl was held on January 15, 1967',
                     'The New England Patriots have won the Super Bowl a record six times']
}
dataset = Dataset.from_dict(data_samples)
score = evaluate(dataset, metrics=[answer_correctness])
df = score.to_pandas()

df.to_csv('answer_correctness.csv', index=False)

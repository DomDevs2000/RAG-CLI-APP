from datasets import Dataset
import os
import pandas as pd
from ragas import evaluate
from ragas.metrics import faithfulness, answer_correctness, context_precision, context_recall, answer_relevancy, \
    context_entity_recall, answer_similarity
from ragas.metrics.critique import harmfulness

# os.environ["OPENAI_API_KEY"] = os.getenv('OPENAI_API_KEY')

data_samples = {
    'question': ['When was the first super bowl?', 'Who won the most super bowls?'],
    'answer': ['The first superbowl was held on Jan 15, 1967',
               'The most super bowls have been won by The New England Patriots'],
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

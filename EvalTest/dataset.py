
import os

os.environ["OPENAI_API_KEY"] = os.getenv("OPENAI_API_KEY")
from langchain_community.document_loaders import DirectoryLoader
loader = DirectoryLoader("./docs/")
documents = loader.load()


from ragas.testset.generator import TestsetGenerator
from ragas.testset.evolutions import simple, reasoning, multi_context
from langchain_openai import ChatOpenAI, OpenAIEmbeddings

# generator with openai models
generator_llm = ChatOpenAI(model="gpt-3.5-turbo")
critic_llm = ChatOpenAI(model="gpt-4o-mini")
embeddings = OpenAIEmbeddings()

generator = TestsetGenerator.from_langchain(
    generator_llm,
    critic_llm,
    embeddings
)

# generate testset
testset = generator.generate_with_langchain_docs(documents, test_size=10, distributions={simple: 0.5, reasoning: 0.25, multi_context: 0.25})
testset.to_pandas()
# testset.to_csv('test.csv', index=False)

from datasets import Dataset
import os
import pandas as pd
import requests
from ragas import evaluate
from ragas.metrics import faithfulness, answer_correctness, context_precision, context_recall, answer_relevancy, \
    context_entity_recall, answer_similarity
from ragas.metrics.critique import harmfulness

dataset = Dataset.load_dataset(testset)

score = evaluate(dataset,
                 metrics=[faithfulness, answer_correctness, context_precision, context_recall, answer_relevancy,
                          context_entity_recall, answer_similarity])
df = score.to_pandas()
df.to_csv('score2.csv', index=False)


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
df = testset.to_pandas()
df.to_csv("rag_dataset.csv", index=False)


# questions = df['question'].tolist()
# contexts = df['contexts'].tolist()
# ground_truths = df['ground_truths'].tolist()
#
# data_samples = {
#     'question': questions,
#     'contexts': contexts,
#     'ground_truth': ground_truths
# }
# test_questions = df['question'].values.tolist()
# test_answers = [[item] for item in df['answer'].values.tolist()]
# print(data_samples)

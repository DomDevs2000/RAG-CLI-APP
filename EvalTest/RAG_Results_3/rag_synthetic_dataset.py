
import os

os.environ["OPENAI_API_KEY"] = os.getenv("OPENAI_API_KEY")
from langchain_community.document_loaders import DirectoryLoader
loader = DirectoryLoader("./docs/")
documents = loader.load()


from ragas.testset.generator import TestsetGenerator
from ragas.testset.evolutions import simple, reasoning, multi_context
from langchain_openai import ChatOpenAI, OpenAIEmbeddings

generator_llm = ChatOpenAI(model="gpt-4-turbo")
critic_llm = ChatOpenAI(model="gpt-4o-mini")
embeddings = OpenAIEmbeddings()

generator = TestsetGenerator.from_langchain(
    generator_llm,
    critic_llm,
    embeddings
)

testset = generator.generate_with_langchain_docs(documents, test_size=10, distributions={simple: 0.5, reasoning: 0.25, multi_context: 0.25})
testset.to_pandas()
df = testset.to_pandas()
df.to_csv("rag_dataset.csv", index=False)


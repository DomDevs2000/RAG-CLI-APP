import os
import pandas as pd
from langchain_community.document_loaders import DirectoryLoader
from ragas.testset.generator import TestsetGenerator
from ragas.testset.evolutions import simple, reasoning, multi_context
from langchain_openai import ChatOpenAI, OpenAIEmbeddings
import os

DATA_PATH = "."

def setup_environment():
    """Configure the environment by setting the OpenAI API key."""
    os.environ["OPENAI_API_KEY"] = os.getenv("OPENAI_API_KEY")
    print("Environment set up with OpenAI API key.")

def load_documents():
    """Load documents from the specified directory using DirectoryLoader."""
    loader = DirectoryLoader("./docs/")
    documents = loader.load()
    print(f"Loaded {len(documents)} documents.")
    return documents

def create_testset_generator():
    """Create and configure the test set generator with OpenAI models."""
    generator_llm = ChatOpenAI(model="gpt-3.5-turbo-16k")
    critic_llm = ChatOpenAI(model="gpt-4o-mini")
    embeddings = OpenAIEmbeddings()
    generator = TestsetGenerator.from_langchain(generator_llm, critic_llm, embeddings)
    print("Test set generator created.")
    return generator

def generate_test_set(documents, test_size=10):
    """Generate a synthetic test set based on the loaded documents."""
    generator = create_testset_generator()
    distributions = {
        simple: 0.5,
        reasoning: 0.25,
        multi_context: 0.25
    }
    testset = generator.generate_with_langchain_docs(documents, test_size, distributions)
    print(f"Generated test set with {test_size} questions.")
    return testset

def export_to_dataframe(testset):
    """Export the generated test set to a pandas DataFrame."""
    test_df = testset.to_pandas()
    print("Test set exported to DataFrame.")
    return test_df

def save_to_csv(test_df, filename):
    """Save the DataFrame to a CSV file."""
    test_df.to_csv(filename, index=False)
    print(f"Test set saved to {filename}.")

def main():
    setup_environment()
    documents = load_documents()
    testset = generate_test_set(documents)
    test_df = export_to_dataframe(testset)
    save_to_csv(test_df, "test_set.csv")

if __name__ == "__main__":
    main()

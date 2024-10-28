# MSc Project - Retrieval Augmented Generation (RAG) pipeline

This project was built using Java, Spring Boot, Spring AI, Python, Docker and AWS.

-Designed and implemented a RAG pipeline to query an LLM to generate financial information from user-uploaded
documents such as Annual Reports, Quarterly Reports, SEC filings etc.
- Ensured RESTful API endpoints to handle user prompts, file uploads and return generated information.
- Performed evaluation testing using Python to gather statistical data such as answer similarity, correctness and
relevance, resulting in 97% answer relevancy and 95% similarity.
- Documents are stored in a PostgreSQL vector store created using JDBC and AWS RDS.
- Implemented Concurrency for file processing, decreasing processing time by 70%.
- Developed a user-friendly CLI with Spring Shell, enabling efficient command execution.
- Created data visualisation dashboards using Python, Pandas, Matplotlib and Jupyter Notebook.
- Ensured a consistent development, testing, and deployment environment using Docker, ECR, ECS and RDS.
- Part of an investigative and development report to critically analyse LLMs and Generative AI, which concluded
that RAG is a great way to increase the accuracy and reliability of LLMs whilst reducing hallucinations,
without the need to fine-tune or train your own model.

# Inverted Index and Web Crawler Project

## Project Overview

This project is completed in three phases:

### Phase One

Builds an inverted index for the text files in a given directory and accepts a number of keywords in a query, returning the files that contain all keywords using the inverted index.

### Phase Two

1. **BI_word Index**:
   - Index each word alone and with an underscore with the next word.
   - For example, the document "automated retrieval of specific information" will be indexed as:
     - automated, retrieval, automated_retrieval, of, retrieval_of, specific, of_specific, information, specific_information
   - The query "specific information" will return the document.

2. **Mix Single Words with Phrases**:
   - For example, the query "automated 'specific information'" will return documents containing the exact phrase "specific information" alongside the word "automated".

3. **Positional Index**:
   - Include a positional index such that specific phrases are identified based on word positions within documents.
   - For example, the query "Cairo University Zayed City" will return documents with that exact sequence of words in order.

### Phase Three

1. **Web Crawler**:
   - Crawl Wikipedia starting from the provided seed URLs.
   - Seed URLs:
     - [List of pharaohs](https://en.wikipedia.org/wiki/List_of_pharaohs)

2. **Query Processing**:
   - Accept a query (set of words) and compute the cosine similarity between each file and the query.
   - Rank the top 10 files according to the value of the cosine similarity.

## Project Structure


- `DictEntry.java`: Represents dictionary entries used in the inverted index.
- `Index5.java`: Builds and manages the inverted index.
- `Posting.java`: Represents the postings list in the inverted index.
- `SortedScore.java`: Handles the sorted scores for document ranking.
- `SourceRecord.java`: Manages the source records for indexing.
- `Stemmer.java`: Implements stemming for word normalization.
- `Test.java`: Run the program on different types of Index.
- `WebCrawlerWithDepth.java`: Implements the web crawler with depth control.

## Usage

### For the Index:

1. **Build the Index**:
   - Change the directory path in the `Test.java` file to your collection directory.
   - Run `Test.java` to build the inverted index from the text files in the specified directory.

2. **Query the Index**:
   - Use the command line to input the type of index you want to use:
     - `1` for inverted index
     - `2` for BI_word index
     - `3` for positional index
   - Enter the search phrase to query for files containing specific keywords.


### For the Web Crawler

- **Web Crawler**:
   - Run `WebCrawlerWithDepth.java` to crawl Wikipedia starting from the provided seed URLs.
   - Extract content and build an index.
   - Enter the search phrase to get the top 10 files containing specific keywords


## Contributors

We would like to thank the following contributors to this project:

- [**Shahd Osama**](https://github.com/shahdosama10).
- [**Shahd Mostafa**](https://github.com/ShahdMostafa30).
- [**Maryam Osama**](https://github.com/maryamosama33).
- [**Ahmed Saad**](https://github.com/ahmedsaad123456).
- [**Seif Ibrahim**](https://github.com/Seif-Ibrahim1).

---

Feel free to contribute to this project by opening issues or submitting pull requests.

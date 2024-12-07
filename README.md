Example of a simple secure CMS using an object store and ELK as the meta data layer


example of POST and GET services

POST http://localhost:8080/api/documents
Body
{
  "filename": "test.txt",
  "content": "SGVsbG8gV29ybGQ=",
  "contentType": "text/plain",
  "metadata": {
    "author": "John Doe",
    "department": "IT"
  }
}
content is hell world encoded in Base64

GET http://localhost:8080/api/documents/{storageKey}/download
to download a document from S3

GET http://localhost:8080/api/documents/8415b221-721c-42c9-acd3-e66ec19d419d
to retrieve all metaData from Elastic

to launch the project
- start S3, Elastic and Kibana with docker-compose up -d
the respective ports are 9001, 9200 and 5601
- Launch SpringBoot


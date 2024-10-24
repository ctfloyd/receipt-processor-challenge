#### Building and Running The Application

Use Docker:
1. `docker build -t receipt-processor .`
2. `docker run -p 8080:8080 -t receipt-processor`

The application is now running on port 8080, query endpoints:
1. `curl -H 'Content-Type: application/json' -d '{"retailer":"M&M Corner Market","purchaseDate":"2022-03-20","purchaseTime":"14:33","items":[{"shortDescription":"Gatorade","price":"2.25"},{"shortDescription":"Gatorade","price":"2.25"},{"shortDescription":"Gatorade","price":"2.25"},{"shortDescription":"Gatorade","price":"2.25"}],"total":"9.00"}' http://localhost:8080/receipts/process`
2. `curl http://localhost:8080/receipts/<receipt-id>/points`

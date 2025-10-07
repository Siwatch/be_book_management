# Getting Started

# Book Management API
โปรเจ็ค RESTful API สำหรับบริหารจัดการหนังสือด้วย Spring Boot 3, Flyway และ ฐานข้อมูล MySQL
โปรเจกต์นี้ถูกพัฒนาขึ้นเพื่อสาธิตแนวทางการเขียนโค้ดให้สะอาดเข้าใจง่าย (Clean Code) การตรวจสอบข้อมูลก่อนบันทึก (Validation) รวมถึงการทดสอบแบบ Integration และการดีพลอยด้วย Docker

(A simple RESTful API for managing books information using Spring Boot 3, Flyway, and MySQL.  
This project demonstrates clean code, input validation, integration testing, and Docker-based deployment.)

### ความต้องการของระบบ (System Requirement)
* Java JDK Version 21 or higher
* Docker Engine or Docker Desktop
* Docker compose
* Spring Boot 3.5.6
* Maven

### วิธีการติดตั้งระบบและฐานข้อมูล 
1.) ทำการ Clone Repository ด้วยคำสั่ง 

    git clone https://github.com/Siwatch/be_book_management
2.) ทำการรัน Docker หรือเปิด Docker Desktop 

3.) ไปที่ตำแหน่ง root ของ repository 

4.) รันคำสั่ง 

    docker compose up --build -d 
    
5.) จากนั้นรอจนกว่า docker จะ build และ รันระบบเสร็จ

### หรือหากไม่มี docker compose สามารถใช้งาน docker ได้โดย
1.) ใช้คำสั่ง (สร้าง MySQL container) 
    
    docker run -d --name mysql-container -p 3306:3306 -e MYSQL_ROOT_PASSWORD=admin -e MYSQL_DATABASE=book -e MYSQL_USER=spring-user -e MYSQL_PASSWORD=spring-secret mysql:latest 
    
2.) ตรวจสอบว่า container ทำงานปกติไม่มี Error โดยการใช้คำสั่ง 

    docker ps -a 
    
และหา container names "mysql-container" ดู status ให้เป็น Up แปลว่า container ทำงานได้ปกติ

3.) ทำการรัน Spring Application โดยใช้คำสั่ง 

    ./mvnw spring-boot:run

### สำหรับ การ setup database จะเป็นการใช้งาน Flyway Migration ของ Spring Boot
โดยการทำงานของ flyway มันจะทำงานเมื่อรัน application spring boot ขึ้นมาโดยจะอ่าน file SQL script จาก /src/main/resources/db/migration โดยใน folder ได้ทำการใส่ SQL script สำหรับสร้าง Table(books) และ ทำ Index(author) เอาไว้

หากต้องการสร้าง SQL script เพิ่มให้ทำการสร้างไฟล์ SQL โดยมี pattern ของชื่อไฟล์ดังนี้ V3__ชื่อที่ต้องการใช้ของไฟล์ (โดยตัวเลขจะเป็น run number 2,3,4,5,....n ไปเรื่อย ๆ โดยที่หาก flyway เคยทำการรันไฟล์ไหนไปแล้วจะไม่ทำการรันซ้ำอีกตรวจสอบด้วย table flyway_schema_history ในฐานข้อมูล) 

### วิธีการรัน Integration tests

ก่อนรัน Integration Test ต้องมั่นใจว่า:
* MySQL container กำลังทำงานอยู่ (ใช้คำสั่ง docker ps เพื่อตรวจสอบสถานะของ MySQL container)
* Database `book` ถูกสร้างโดย Flyway เรียบร้อยแล้ว (จะต้องทำการรัน Spring Boot Application มาก่อนขั้นตอนนี้)

ใช้คำสั่ง

    ./mvnw test

Spring Boot จะทำการรันไฟล์ เทสทั้งหมดที่อยู่ใน โฟลเดอร์ /src/test โดยจะมีเคสทั้งหมด 14 test cases

* 1 case - BookManagementApplicationTests
* 3 case - Integration Test (Note ในขณะรัน integration test จำเป็นต้องให้ MySQL รันอยู่ตลอดเวลา)
* 10 case - Unit Test

### Example API requests and expected responses.

| Method  | Endpoint                | Description             |
|---------|-------------------------|-------------------------|
| GET     | /books                  | Get all books           |
| GET     | /books?author={author}  | Get books by author     |
| POST    | /books                  | Create a new book       |

#### API Endpoint

API นี้ใช้สำหรับบริหารจัดการข้อมูลหนังสือ เช่น เพิ่มหนังสือใหม่ ดึงข้อมูลทั้งหมด หรือค้นหาตามผู้แต่ง  
โดยทุก request จำเป็นต้องส่งวันที่ในรูปแบบ **พุทธศักราช (BE)** ซึ่งระบบจะทำการแปลงเป็น **คริสต์ศักราช (CE)** และตรวจสอบความถูกต้องให้อัตโนมัติ
(This API allows clients to manage book information — including creating new books, retrieving all books, or searching by author.  
All dates must be provided in the Buddhist calendar (BE), and validation will automatically handle leap years and convert to Common Era (CE) internally)

1.) Get All Books

Request

    GET http://localhost:8080/books

Example using curl

    curl -X GET 'http://localhost:8080/books'

Expected Response

    {
        "statusCode": "OK",
        "message": null,
        "data": [
            {
                "id": 1,
                "title": "Java Basics",
                "author": "John Doe",
                "publishedDate": "2568-02-28"
            },
            {
                "id": 2,
                "title": "Java Basics 2",
                "author": "John Doe",
                "publishedDate": "2568-02-28"
            },
            {
                "id": 3,
                "title": "Spring boot API",
                "author": "Jane Smith",
                "publishedDate": "2566-01-31"
            }
        ]
    }

2.) Get Books By Author

Request

    GET http://localhost:8080/books?author={author}

Example using curl

    curl -X GET 'http://localhost:8080/books?author=John%20Doe'

Expected Response

    {
        "statusCode": "OK",
        "message": null,
        "data": [
            {
                "id": 1,
                "title": "Java Basics",
                "author": "John Doe",
                "publishedDate": "2568-02-28"
            },
            {
                "id": 2,
                "title": "Java Basics 2",
                "author": "John Doe",
                "publishedDate": "2568-02-28"
            },
        ]
    }

3.) Create a New Book

Request

    POST http://localhost:8080/books
    Content-Type: application/json

Request Body

    {
        "title": "Java Basic 3",
        "author": "John Doe",
        "publishedDate": "2568-10-07"
    }


Example using curl

    curl -X POST 'http://localhost:8080/books' \
         --header 'Content-Type: application/json' \
         --data '{
                    "title": "Java Basic 3",
                    "author": "John Doe",
                    "publishedDate": "2568-10-07"
                }'

Expected Response 

    {
        "statusCode": "OK",
        "message": "Create Book Success!!",
        "data": null
    }


Error Example

If Request is Invalid 

    POST /api/books
    {
        // empty body request 
    }

Expected Response

    {
        "statusCode": "BAD_REQUEST",
        "error": "VALIDATION_ERROR",
        "message": "Missing or invalid required fields",
        "fields": [
                    {
                        "field": "title",
                        "message": "title is required"
                    },
                    {
                        "field": "author",
                        "message": "author is required"
                    }
                ]   
    } 

If `publishedDate` is invalid

    POST /api/books
    {
        "title": "Spiderman and friends",
        "author": "Peter Parker",
        "publishedDate": "2569-02-28" // ปีมากกว่าปัจจุบัน (ปัจจุบันปี 2568)
    }

Expected Response 

    {
        "statusCode": "BAD_REQUEST",
        "error": "BUSINESS_ERROR",
        "message": "Published year (BE 2569 / CE 2026) must be between 1000 and 2025",
        "fields": null
    }





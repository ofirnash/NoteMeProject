"use strict";
const express = require('express');
const bodyParser = require('body-parser');
const app = express();
const fs = require('fs');
const PORT = 8080;
app.use(bodyParser.json());

const MongoClient = require('mongodb').MongoClient;
const MONGO_URL = "mongodb://localhost:27017";
MongoClient.connect(MONGO_URL, {useNewUrlParser:true}, function(err,client){
    if (err)
        console.log('Unable to connect to mongoDB', err);
    else {
        //Sign up
        app.post('/signup', (req,res,next) =>{
            let email = req.body.email;
            let userName = req.body.userName;
            let password = req.body.password;

            let insertJSON = {
                'email': email,
                'userName': userName,
                'password': password
            };
            let db = client.db('noteMeDB');
            
            //Check if username already exists in DB (username should be unique)
            db.collection('users')
                .find({'userName':userName}).count(function(err,number){
                    if (number != 0){
                        res.json('User name already exists');
                        console.log('User name already exists');
                    } else {
                        //Insert new user to DB
                        db.collection('users').insertOne(insertJSON, function(error,response){
                            res.json('User signed up successfully');
                            console.log('User signed up successfully');
                        })
                    }
                })
        });

        //Login
        app.post('/login',(req,res,next) => {
            let userName = req.body.userName;
            let password = req.body.password;

            let db = client.db('noteMeDB');

            //Check if email exists
            db.collection('users')
                .find({'userName':userName}).count(function(err,number){
                    if (number == 0){
                        res.json('User does not exists');
                        console.log('User does not exists');
                    } else {
                        db.collection('users')
                            .findOne({'userName':userName}, function(err,user){
                                 if (user.password == password){
                                     res.json('User legged in successfully');
                                     console.log('User logged in successfully');
                                 } else {
                                     res.json('Login failed, wrong password');
                                     console.log('Login failed, wrong password');
                                 }
                            })
                    }
                })
        });

        //addNote
        app.post('/addnote', (req,res,next) => {
            let userName = req.body.userName;
            let title = req.body.title;
            let description = req.body.description;
            let image = req.body.image;
            let latitude = req.body.latitude;
            let longitude = req.body.longitude;

            let insertJSON = {
                'userName': userName,
                'title': title,
                'description': description,
                'image': image,
                'latitude': latitude,
                'longitude': longitude,
                'likes': 0
            };

            let db = client.db('noteMeDB');
            db.collection('notes').insertOne(insertJSON, function(error,response){
                res.json('Note was added successfully');
                console.log('Note was added successfully');
            })
        });

        //getNote (based on the marker lat and long)
        app.get('/getnote', (req,res,next) => {
            let latitude = req.query.latitude;
            let longitude = req.query.longitude;
            let db = client.db("noteMeDB");
            let query = {"latitude": latitude, "longitude":longitude};
            let resJSON = "";
            db.collection("notes").find(query).toArray(function(err,result){
                    if (result.length != 0){
                        console.log(`Successfuly found ${result.length} notes`);
                        resJSON = JSON.stringify(result);
                    } else {
                        console.log("Couldn't find note");
                    }
                    if (err) throw err;
                });
            res.send(resJSON);
        });
        
        //getAllNotes (of a specific user)
        app.get('/getallnotes', (req,res,next) => {
            let userName = req.query.username;
            let db = client.db("noteMeDB");
            let query = {"userName": userName};
            let resJSON = "";
            db.collection("notes").find(query).toArray(function(err,result){
                    console.log(`Successfuly found ${result.length} notes`);
                    resJSON = JSON.stringify(result);
                    if (err) throw err;
                });
            res.send(resJSON);
        });

        //addMarker
        app.post('/addmarker', (req,res,next) => {
            let latitude = req.body.latitude;
            let longitude = req.body.longitude;

            let insertJSON = {
                'latitude': latitude,
                'longitude': longitude
            };

            let db = client.db('noteMeDB');
            db.collection('markers').insertOne(insertJSON, function(error,response){
                res.json('Marker was added successfully');
                console.log('Marker was added successfully');
            })
        });

        //getAllMarkers
        app.get('/getallmarkers', (req,res,next) => {
            let db = client.db("noteMeDB");
            let resJSON = "";
            db.collection("markers").find({}).toArray(function(err,result){
                    console.log(`Successfuly found ${result.length} markers`);
                    resJSON = JSON.stringify(result);
                    if (err) throw err;
            });
            res.send(resJSON);
        });

        //addLike
        app.put('/addlike', (req,res,next) => {
            let latitude = req.query.latitude;
            let longitude = req.query.longitude;
            let db = client.db("noteMeDB");
            let query = {"latitude": latitude, "longitude":longitude};
            let newValue = {$inc: {likes: 1}};
            db.collection("notes").updateOne(query,newValue, function(err,res){
                if (err) throw err;
                console.log("Note likes counter was increased by 1");
            });
        });

        //removeLike
        app.put('/removelike', (req,res,next) => {
            let latitude = req.query.latitude;
            let longitude = req.query.longitude;
            let db = client.db("noteMeDB");
            let query = {"latitude": latitude, "longitude":longitude};
            let newValue = {$inc: {likes: -1}};
            db.collection("notes").updateOne(query,newValue, function(err,res){
                if (err) throw err;
                console.log("Note likes counter was decreased by 1");
            });
        });

        //Start Web Server
        app.listen(PORT,() => {
            console.log('Connected to mongoDB and listening on port %d!',PORT);
        });
    }
});




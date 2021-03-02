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
            let firstName = req.body.firstName;
            let lastName = req.body.lastName;
            let email = req.body.email;
            let password = req.body.password;

            let insertJSON = {
                'firstName': firstName,
                'lastName': lastName,
                'email': email,
                'password': password
            };
            console.log(insertJSON);
            let db = client.db('noteMeDB');
            
            //Check if email already exists in DB
            db.collection('users')
                .find({'email':email}).count(function(err,number){
                    if (number != 0){
                        res.json('Email already exists');
                        console.log('Email already exists');
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
            console.log(req);
            let email = req.body.email;
            let password = req.body.password;

            let db = client.db('noteMeDB');

            //Check if email exists
            db.collection('users')
                .find({'email':email}).count(function(err,number){
                    if (number == 0){
                        res.json('Email does not exists');
                        console.log('Email does not exists');
                    } else {
                        db.collection('users')
                            .findOne({'email':email}, function(err,user){
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
            let user = req.body.user;
            let title = req.body.title;
            let description = req.body.description;
            let image = req.body.image;
            let latitude = req.body.latitude;
            let longitude = req.body.longitude;

            let insertJSON = {
                'user': user,
                'title': title,
                'description': description,
                'image': image,
                'latitude': latitude,
                'longitude': longitude
            };

            let db = client.db('noteMeDB');
            db.collection('notes').insertOne(insertJSON, function(error,response){
                res.json('Note was added successfully');
                console.log('Note was added successfully');
            })
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

            //Check if marker exists
            db.collection('markers')
                .find({'latitude': latitude}).count(function(err,number){
                    console.log(`Number = ${number}`);
                    if (number == 0){
                        db.collection('markers').insertOne(insertJSON, function(error,response){
                            res.json('Marker was added successfully');
                            console.log('Marker was added successfully');
                        })
                    } else {
                        db.collection('markers')
                            .find({'longitude': longitude}).count(function(err,number){
                                if (number != 0){
                                    res.json('Marker already exists');
                                    console.log('Marker already exists');
                                } else {
                                    db.collection('markers').insertOne(insertJSON, function(error,response){
                                        res.json('Marker was added successfully');
                                        console.log('Marker was added successfully');
                                    })
                                }
                            })
                    }
                });
            
        });

        //Start Web Server
        app.listen(PORT,() => {
            console.log('Connected to mongoDB and listening on port %d!',PORT);
        });
    }
});




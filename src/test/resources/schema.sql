CREATE TABLE PET
(
    ID INT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    PET_NAME VARCHAR(20) NOT NULL DEFAULT 'NoName'
);
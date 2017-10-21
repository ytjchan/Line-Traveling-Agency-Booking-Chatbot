/* Drop tables to avoid name conflict*/
drop table Tour cascade;
drop table TourOffering cascade;
drop table TOrelation cascade;
drop table TourGuide cascade;
drop table OGrelation cascade;
drop table Booker cascade;
drop table Booking cascade;
drop table Question cascade;
drop table FAQ cascade;

/* Creating tables */
create table Tour (TourID varchar(5) constraint check_id_length check (length(TourID)=5) primary key, TourName varchar(50) constraint not_null not null, TourDesc varchar(200), TourLength smallint constraint check_length check (TourLength>0));
create table TourOffering (OfferID varchar(13) constraint check_id_length check(length(OfferID)=13) primary key, Date date, Hotel varchar(50), Price smallint constraint check_price check (Price>0), MaxCapacity smallint constraint check_capacity check (MaxCapacity>0 and MaxCapacity>MinRequirement), MinRequirement smallint constraint check_requirement check (MinRequirement>0), Confirmed boolean);
create table TOrelation (TourID varchar(5) references Tour(TourID), OfferID varchar(13) references TourOffering(OfferID), primary key (TourID, OfferID));
create table TourGuide (LineID varchar(40) primary key, Name varchar(50));
create table OGrelation (OfferID varchar(13) references TourOffering(OfferID), LineID varchar(40) references TourGuide(LineID), primary key(OfferID, LineID));
create table Booker (LineID varchar(40) primary key, Name varchar(50), HKID varchar(7), PhoneNo integer, Age smallint constraint check_age check (Age>0));
create table Booking (BookingID varchar(20) primary key,  LineID varchar(40) references Booker(LineID) not null, OfferID varchar(13) references TourOffering(OfferID) not null, Adults smallint, Children smallint, Toddlers smallint, AmountPaid real, SpecialRequest varchar(100), Cancelled boolean default false);
create table Question (QID integer primary key, LineID varchar(40) references Booker(LineID) not null, FullQuestion varchar(100) not null, LastFiveQuestion varchar(500));
create table FAQ (Keyword varchar(50) primary key, Question varchar(100), Answer varchar(100) not null);

comment on table Tour is 'All Tour name and description, reused by TourOffering';
comment on table TourOffering is 'Tour offered on a certain day';
comment on table TOrelation is 'Connect Tour&TourOffering (TODO: can be replaced by foreign key in TourOffering)';
comment on table TourGuide is 'Tour guide LineID and name';
comment on table OGrelation is 'Connect Tour&TourGuide (TODO: can be replaced by foreign key in TourOffering)';
comment on table Booker is 'Individual Bookers with LineID and personal info';
comment on table Booking is 'Booking of bookers on a certain TourOffering, with booking details';
comment on table Question is 'Unanswered questions from users';
comment on table FAQ is 'FAQs for chatbot to search through';

/* Insertion sample */
/* insert into tour values ('1A001', 'Happy', 'Oh no', 3);
insert into touroffering values ('1234567890123', '2017-1-1', 'UST Hall 5', 500, 20, 4);
insert into torelation values ('1A001', '1234567890123');
insert into tourguide values ('jeffer', 'Jeffer Chan');
insert into ogrelation values ('1234567890123', 'jeffer');
insert into booker values ('12345678', 'Jeffer Chan', 'A123456', 98765432, 20);
insert into booking values ('1234567890', '12345678', '1234567890123', 2, 0, 0, 100, 'No smoking room');
insert into question values (1, '12345678', 'HEY LISTEN', 'I want to book.*I wanna go there.*Please book this.*Search for zoos.*Go to hell');
insert into faq values ('Hi','Greetings', 'Hello World from COMP3111 Group 12'); */
\d+;

/* Clean up */
drop table Tour cascade;
drop table TourOffering cascade;
drop table TourGuide cascade;
drop table Booker cascade;
drop table Booking cascade;
drop table Question cascade;
drop table FAQ cascade;
drop table Staff;

/* Create tables */
create table Tour (TourID varchar(5) constraint check_id_length check (length(TourID)=5) primary key, TourName varchar(50) constraint not_null not null, TourDesc varchar(200), TourLength smallint constraint check_length check (TourLength>0));
create table TourGuide (LineID varchar(40) primary key, Name varchar(50));
create table TourOffering (TourID varchar(5) references Tour(TourID), OfferID varchar(8) constraint check_id_length check(length(OfferID)=8), TourDate date, TourGuideLineID varchar(40) references TourGuide(LineID) not null, Hotel varchar(50), Price smallint constraint check_price check (Price>0), MaxCapacity smallint constraint check_capacity check (MaxCapacity>0 and MaxCapacity>MinRequirement), MinRequirement smallint constraint check_requirement check (MinRequirement>0), Confirmed boolean, primary key(TourID, OfferID));
create table Booker (LineID varchar(40) primary key, Name varchar(50), HKID varchar(7), PhoneNo integer, Age smallint constraint check_age check (Age>0));

/* changed for extra feature */
/* create table Booking (LineID varchar(40) references Booker(LineID) not null, OfferID varchar(13) not null, Adults smallint, Children smallint, Toddlers smallint, TourFee real, AmountPaid real, SpecialRequest varchar(500), Cancelled boolean default false, primary key (LineID,OfferID));*/
create table Booking (
	LineID varchar(40) references Booker(LineID) not null,
	OfferID varchar(13) not null, 
	Adults smallint, 
	Children smallint, 
	Toddlers smallint, 
	TourFee real, 
	AmountPaid real, 
	SpecialRequest varchar(500), 
	Cancelled boolean default false, 
	bookingid serial primary key
);

create table Question (QID integer primary key, LineID varchar(40) not null, FullQuestion varchar(200) not null, LastFiveQuestion varchar(1000), Resolved boolean default false);
create table FAQ (Keyword varchar(200) primary key, Question varchar(200), Answer varchar(500) not null);
create table Staff(UserID varchar(50) primary key);

/* new table for extra feature */
create table Discount (
	TourID  varchar(5),
	OfferID varchar(8),
	Discount real not null,
	Count int not null,
	Remaining int not null,
	foreign key(TourID, OfferID) references touroffering(tourid,offerid)
);

/* Description for tables */
comment on table Tour is 'All Tour name and description, reused by TourOffering';
comment on table TourGuide is 'Tour guide LineID and name, reused by TourOffering';
comment on table TourOffering is 'Tour offered on a certain day';
comment on table Booker is 'Individual Bookers with LineID and personal info';
comment on table Booking is 'Booking of bookers on a certain TourOffering, with booking details';
comment on table Question is 'Unanswered questions from users';
comment on table FAQ is 'FAQs for chatbot to search through';
comment on table Staff is 'All userId of staffs to push message to them';

/* Demostration of inserting records */
/*insert into tour values ('1A001', 'Happy', 'Oh no', 3);
insert into tourguide values ('jefferlineid', 'Jeffer Chan');
insert into touroffering values ('1A001', '00000001', '2017-1-1', 'jefferlineid', 'UST Hall 5', 500, 20, 4);
insert into booker values ('1234567890', 'Jeffer Chan', 'A123456', 98765432, 20);
insert into booking values ('1234567890', '1A00112345678', '00000001', 2, 0, 0, 100, 'No smoking room');
insert into question values (1, '1234567890', 'HEY LISTEN', 'I want to book.*I wanna go there.*Please book this.*Search for zoos.*Go to hell');
insert into faq values ('Hi','Greetings', 'Hello World from COMP3111 Group 12');*/
\d+;


/* Tour table from "Tour List"*/
INSERT INTO Tour (TourID,TourName,TourDesc,TourLength) VALUES ('2D001','Shimen National Forest Tour','Shimen colorful pond * stunning red maple * Staying at \"Yihua Hot Spring Hotel\"','2');
INSERT INTO Tour (TourID,TourName,TourDesc,TourLength) VALUES ('2D002','Yangshan Hot Spring Tour','Unlimited use of hot spring * Famous Yangshan roaster cusine','2');
INSERT INTO Tour (TourID,TourName,TourDesc,TourLength) VALUES ('2D003','Heyuan Hotspring Tour ','Five Stars Hot Spring Resort * 68 different types of hot spring * Water theme park','2');
INSERT INTO Tour (TourID,TourName,TourDesc,TourLength) VALUES ('2D004','National Park Tour','Green Lake District (Ferry Tour Included) * Asia tallest musical fountain * Staying at 4 stars hotel ','2');
INSERT INTO Tour (TourID,TourName,TourDesc,TourLength) VALUES ('2D005','Yummy Sight seeing Tour','Once-a-year beautiful \"Four-fins-maple\" on mountain top * BBQ chicken and piglet in a lost traditional way ','2');
INSERT INTO Tour (TourID,TourName,TourDesc,TourLength) VALUES ('3D019','Guangdong country-side relax trip ','Baiyun Mountain * the Site of the Huangpu Military Academy * the South China Botanical Gardens.  * 4 stars hotel','3');
INSERT INTO Tour (TourID,TourName,TourDesc,TourLength) VALUES ('3D075','Kaipinng culture tour ','Kaiping Watchtowers * Jinshan Hot Spring * 5 starts hotel','3');
INSERT INTO Tour (TourID,TourName,TourDesc,TourLength) VALUES ('3D077','Shaoguan sight-seeing tour','Nanhua Temple * Danxia Mountain * Nanling National Forest Park','3');
INSERT INTO Tour (TourID,TourName,TourDesc,TourLength) VALUES ('3D010','Dynamic Zhuhai-Macau tour','Jinding * Tangjiawan * Gongbei * Including one night staying at 5-stars casino resort in Macau','3');
INSERT INTO Tour (TourID,TourName,TourDesc,TourLength) VALUES ('3D991','Qingyuan historic-landscape tour ','Baojing Palace of Yingde * Sankeng Hot Spring * Sankeng Hot Spring ','3');
INSERT INTO Tour (TourID,TourName,TourDesc,TourLength) VALUES ('3D842','Shenzhen city tour ','Window of The World  * Splendid China & Chinese Folk Culture Village * Dafen Oil Painting Village (All tickets included)','3');

/* TourGuide table from "Bookin Table" */
INSERT INTO TourGuide (LineID,Name) VALUES ('hkgeorgechen','Amber');
INSERT INTO TourGuide (LineID,Name) VALUES ('hkjeffer','Betty');
INSERT INTO TourGuide (LineID,Name) VALUES ('memberc','Carol');
INSERT INTO TourGuide (LineID,Name) VALUES ('memberl','Linsay');

/* TourOffering table from "Booking Table" notice that escape character in psql is ' (single quotation mark) */
INSERT INTO touroffering (tourid,offerid,tourdate,tourguidelineid,hotel,price,maxcapacity,minrequirement) VALUES ('2D001','20171106','2017-06-11','hkgeorgechen','Hotel L''Cheap','599','20','4');
INSERT INTO touroffering (tourid,offerid,tourdate,tourguidelineid,hotel,price,maxcapacity,minrequirement) VALUES ('2D001','20171108','2017-08-11','hkjeffer','Hotel L''Cheap','499','20','4');
INSERT INTO touroffering (tourid,offerid,tourdate,tourguidelineid,hotel,price,maxcapacity,minrequirement) VALUES ('2D001','20171111','2017-11-11','memberc','Hotel de Carol','599','20','4');
INSERT INTO touroffering (tourid,offerid,tourdate,tourguidelineid,hotel,price,maxcapacity,minrequirement) VALUES ('2D001','20171113','2017-11-13','memberl','Hotel L''Cheap','499','20','4');
INSERT INTO touroffering (tourid,offerid,tourdate,tourguidelineid,hotel,price,maxcapacity,minrequirement) VALUES ('2D001','20171115','2018-11-15','hkgeorgechen','Hotel de Carol','499','20','4');
INSERT INTO touroffering (tourid,offerid,tourdate,tourguidelineid,hotel,price,maxcapacity,minrequirement) VALUES ('2D001','20171118','2017-11-18','memberc','Maxim Grand Hotel','599','20','4');
INSERT INTO touroffering (tourid,offerid,tourdate,tourguidelineid,hotel,price,maxcapacity,minrequirement) VALUES ('2D002','20171106','2017-06-11','memberc','Hotel L''Cheap','399','30','8');
INSERT INTO touroffering (tourid,offerid,tourdate,tourguidelineid,hotel,price,maxcapacity,minrequirement) VALUES ('2D002','20171108','2017-08-11','memberl','Hotel de Carol','299','30','8');
INSERT INTO touroffering (tourid,offerid,tourdate,tourguidelineid,hotel,price,maxcapacity,minrequirement) VALUES ('2D002','20171117','2017-11-17','memberc','Maxim Grand Hotel','299','30','8');
INSERT INTO touroffering (tourid,offerid,tourdate,tourguidelineid,hotel,price,maxcapacity,minrequirement) VALUES ('2D003','20171016','2017-10-16','memberc','Hell Hotel','299','50','20');
INSERT INTO touroffering (tourid,offerid,tourdate,tourguidelineid,hotel,price,maxcapacity,minrequirement) VALUES ('2D003','20171014','2017-10-14','memberl','Heaven Hotel','399','30','8');
INSERT INTO touroffering (tourid,offerid,tourdate,tourguidelineid,hotel,price,maxcapacity,minrequirement) VALUES ('2D005','20171010','2017-10-10','memberc','Heaven Hotel','399','20','4');
INSERT INTO touroffering (tourid,offerid,tourdate,tourguidelineid,hotel,price,maxcapacity,minrequirement) VALUES ('2D005','20171021','2017-10-21','memberl','Hell Hotel','499','30','8');
INSERT INTO touroffering (tourid,offerid,tourdate,tourguidelineid,hotel,price,maxcapacity,minrequirement) VALUES ('3D019','20171028','2017-10-28','memberc','Hell Hotel','799','50','20');
INSERT INTO touroffering (tourid,offerid,tourdate,tourguidelineid,hotel,price,maxcapacity,minrequirement) VALUES ('3D019','20171025','2017-10-25','memberl','Heaven Hotel','599','10','2');
INSERT INTO touroffering (tourid,offerid,tourdate,tourguidelineid,hotel,price,maxcapacity,minrequirement) VALUES ('3D075','20171211','2017-12-11','memberc','Heaven Hotel','699','10','2');
INSERT INTO touroffering (tourid,offerid,tourdate,tourguidelineid,hotel,price,maxcapacity,minrequirement) VALUES ('3D075','20171216','2017-12-16','memberl','Heaven Hotel','899','30','8');
INSERT INTO touroffering (tourid,offerid,tourdate,tourguidelineid,hotel,price,maxcapacity,minrequirement) VALUES ('2D004','20171115','2017-11-15','memberc','Hell Hotel','299','40','12');
INSERT INTO touroffering (tourid,offerid,tourdate,tourguidelineid,hotel,price,maxcapacity,minrequirement) VALUES ('2D004','20171111','2017-11-11','memberl','Heaven Hotel','399','50','20');
INSERT INTO touroffering (tourid,offerid,tourdate,tourguidelineid,hotel,price,maxcapacity,minrequirement) VALUES ('3D077','20171114','2017-11-14','memberc','Heaven Hotel','499','20','4');
INSERT INTO touroffering (tourid,offerid,tourdate,tourguidelineid,hotel,price,maxcapacity,minrequirement) VALUES ('3D077','20171116','2017-11-16','memberl','Heaven Hotel','499','20','4');
INSERT INTO touroffering (tourid,offerid,tourdate,tourguidelineid,hotel,price,maxcapacity,minrequirement) VALUES ('3D010','20171122','2017-11-22','memberc','Heaven Hotel','699','40','12');
INSERT INTO touroffering (tourid,offerid,tourdate,tourguidelineid,hotel,price,maxcapacity,minrequirement) VALUES ('3D010','20171118','2017-11-18','memberl','Hell Hotel','899','50','20');
INSERT INTO touroffering (tourid,offerid,tourdate,tourguidelineid,hotel,price,maxcapacity,minrequirement) VALUES ('3D991','20171007','2017-10-07','memberc','Hell Hotel','899','20','4');
INSERT INTO touroffering (tourid,offerid,tourdate,tourguidelineid,hotel,price,maxcapacity,minrequirement) VALUES ('3D991','20171017','2017-10-17','memberl','Heaven Hotel','899','30','8');
INSERT INTO touroffering (tourid,offerid,tourdate,tourguidelineid,hotel,price,maxcapacity,minrequirement) VALUES ('3D842','20171129','2017-11-29','memberc','Heaven Hotel','699','50','20');
INSERT INTO touroffering (tourid,offerid,tourdate,tourguidelineid,hotel,price,maxcapacity,minrequirement) VALUES ('3D842','20171126','2017-11-26','memberl','Hell Hotel','899','10','2');

/* Booker table from "Customer Table" */
INSERT INTO Booker (LineId,Name,HKID,PhoneNo,Age) VALUES ('hkgeorgechen','Peppa PIG','A111222','66161748','35');
INSERT INTO Booker (LineId,Name,HKID,PhoneNo,Age) VALUES ('hkjeffer','Emily ELEPHANT','A222444','67679166','36');
INSERT INTO Booker (LineId,Name,HKID,PhoneNo,Age) VALUES ('kimsung','Kim SUNG','A234567','85193501','62');
INSERT INTO Booker (LineId,Name,HKID,PhoneNo,Age) VALUES ('kelvinwang','Kelvin WANG','A234568','56726946','46');
INSERT INTO Booker (LineId,Name,HKID,PhoneNo,Age) VALUES ('potato','Potato','A234569','35746310','40');
INSERT INTO Booker (LineId,Name,HKID,PhoneNo,Age) VALUES ('tomato','Tomato','A234570','58192695','71');
INSERT INTO Booker (LineId,Name,HKID,PhoneNo,Age) VALUES ('flowey','Flowey','A234571','47270278','73');
INSERT INTO Booker (LineId,Name,HKID,PhoneNo,Age) VALUES ('pedobear','Pedo BEAR','A234572','41610757','44');
INSERT INTO Booker (LineId,Name,HKID,PhoneNo,Age) VALUES ('heroine','Heroine','A234573','23469506','65');
INSERT INTO Booker (LineId,Name,HKID,PhoneNo,Age) VALUES ('desmond','Desmond','A234574','34002193','52');
INSERT INTO Booker (LineId,Name,HKID,PhoneNo,Age) VALUES ('kudo','Kudo','A234575','53991278','55');
INSERT INTO Booker (LineId,Name,HKID,PhoneNo,Age) VALUES ('hehe','He HE','A234576','85865342','50');
INSERT INTO Booker (LineId,Name,HKID,PhoneNo,Age) VALUES ('sheshe','She SHE','A234577','48928845','46');
INSERT INTO Booker (LineId,Name,HKID,PhoneNo,Age) VALUES ('itit','It IT','A234578','43329939','44');
INSERT INTO Booker (LineId,Name,HKID,PhoneNo,Age) VALUES ('ill','Ill','A234579','24270379','70');
INSERT INTO Booker (LineId,Name,HKID,PhoneNo,Age) VALUES ('we','We','A234580','34459050','36');
INSERT INTO Booker (LineId,Name,HKID,PhoneNo,Age) VALUES ('you','You','A234581','52196937','74');
INSERT INTO Booker (LineId,Name,HKID,PhoneNo,Age) VALUES ('they','They','A234582','46753591','53');
INSERT INTO Booker (LineId,Name,HKID,PhoneNo,Age) VALUES ('isam','Is AM','A234583','92881923','73');
INSERT INTO Booker (LineId,Name,HKID,PhoneNo,Age) VALUES ('arewedone','Are We DONE','A234584','89356434','53');

/* Booking table from "Customer Table" */
INSERT INTO Booking (LineID,OfferID,Adults,Children,Toddlers,TourFee,AmountPaid,SpecialRequest,Cancelled) VALUES ('hkgeorgechen','2D00120171108',2,2,0,1796.4,1796.4,'Non smoking room',false);
INSERT INTO Booking (LineID,OfferID,Adults,Children,Toddlers,TourFee,AmountPaid,SpecialRequest,Cancelled) VALUES ('hkjeffer','2D00120171108',2,0,0,998,500,null,false);
INSERT INTO Booking (LineID,OfferID,Adults,Children,Toddlers,TourFee,AmountPaid,SpecialRequest,Cancelled) VALUES ('hkjeffer','2D00120171118',2,0,0,998,600,null,false);

/* FAQ table from faq.md */
INSERT INTO FAQ (Keyword,Question,Answer) VALUES ('apply', 'How to apply?', 'Customers shall approach the company by phone or visit our store (in Clearwater bay) with the choosen tour code and departure date. If it is not full, customers will be advised by the staff to pay the tour fee. Tour fee is non refundable. Customer can pay their fee by ATM to 123-345-432-211 of ABC Bank or by cash in our store. Customer shall send their pay-in slip to us by email or LINE.');
INSERT INTO FAQ (Keyword,Question,Answer) VALUES ('gathering', 'Where is the gathering/assemble and dismiss spot?', 'We gather at the gathering spot "Exit A, Futian port, Shenzhen" at 8:00AM on the departure date. We dismiss at the same spot after the tour.');
INSERT INTO FAQ (Keyword,Question,Answer) VALUES ('assemble', 'Where is the gathering/assemble and dismiss spot?', 'We gather at the gathering spot "Exit A, Futian port, Shenzhen" at 8:00AM on the departure date. We dismiss at the same spot after the tour.');
INSERT INTO FAQ (Keyword,Question,Answer) VALUES ('dismiss', 'Where is the gathering/assemble and dismiss spot?', 'We gather at the gathering spot "Exit A, Futian port, Shenzhen" at 8:00AM on the departure date. We dismiss at the same spot after the tour.');
INSERT INTO FAQ (Keyword,Question,Answer) VALUES ('cancel', 'What if the tour is cancelled?', 'In case a tour has not enough people or bad weather condition and the tour is forced to cancel, customers will be informed 3 days in advanced. Either change to another tour or refund is avaliable for customers to select. However, due to other reasons such as customers\ personal problem no refund can be made.');
INSERT INTO FAQ (Keyword,Question,Answer) VALUES ('charge', 'Are there any additional charge?', 'Each customer need to pay an additional service charge at the rate $60/day/person, on top of the tour fee. It is collected by the tour guide at the end of the tour.');
INSERT INTO FAQ (Keyword,Question,Answer) VALUES ('transportation', 'What is the transportation in Guangdong?', 'A tour bus.');
INSERT INTO FAQ (Keyword,Question,Answer) VALUES ('contact', 'How can I contact the tour guide?', 'Each tour guide has a LINE account and he will add the customers as his friends before the departure date. You can contact him/her accordingly.');
INSERT INTO FAQ (Keyword,Question,Answer) VALUES ('tout guide', 'How can I contact the tour guide?', 'Each tour guide has a LINE account and he will add the customers as his friends before the departure date. You can contact him/her accordingly.');
INSERT INTO FAQ (Keyword,Question,Answer) VALUES ('insurance', 'Is insurance covered?', 'Yes, each customers are protected by the Excellent Blue-Diamond Scheme by AAA insurance company.');
INSERT INTO FAQ (Keyword,Question,Answer) VALUES ('hotel', 'Is the hotel single bed or twin bed or double bed?', 'All rooms are twin beds. Every customer needs to share a room with another customer. If a customer prefer to own a room by himself/herself, additional charge of 30% will be applied.');
INSERT INTO FAQ (Keyword,Question,Answer) VALUES ('room', 'Is the hotel single bed or twin bed or double bed?', 'All rooms are twin beds. Every customer needs to share a room with another customer. If a customer prefer to own a room by himself/herself, additional charge of 30% will be applied.');
INSERT INTO FAQ (Keyword,Question,Answer) VALUES ('bed', 'Is the hotel single bed or twin bed or double bed?', 'All rooms are twin beds. Every customer needs to share a room with another customer. If a customer prefer to own a room by himself/herself, additional charge of 30% will be applied.');
INSERT INTO FAQ (Keyword,Question,Answer) VALUES ('visa', 'Visa problem', 'Please refer the Visa issue to the immigration department of China. The tour are assembled and dismissed in mainland and no cross-border is needed. However, you will need a travelling document when you check in the hotel.');
INSERT INTO FAQ (Keyword,Question,Answer) VALUES ('swimming suit', 'Do I need swimming suit in a water them park or a hot spring resort?', 'Yes you do need it. Otherwise you may not use the facility.');
INSERT INTO FAQ (Keyword,Question,Answer) VALUES ('vegeterian', 'Do you serve vegeterian?', 'No');
INSERT INTO FAQ (Keyword,Question,Answer) VALUES ('fee', 'What is the tour fee for children?', 'Age below 3 (including 3) is free. Age between 4 to 11 (including 4 and 11) has a discount of 20% off. Otherwise full fee applies. The same service charge is applied to all age customers');
INSERT INTO FAQ (Keyword,Question,Answer) VALUES ('children', 'What is the tour fee for children?', 'Age below 3 (including 3) is free. Age between 4 to 11 (including 4 and 11) has a discount of 20% off. Otherwise full fee applies. The same service charge is applied to all age customers');
INSERT INTO FAQ (Keyword,Question,Answer) VALUES ('late', 'What if I am late in the departure date?', 'You shall contact the tour guide if you know you will be late and see if the tour guide can wait a little bit longer. No refund or make up shall be made if a customer is late for the tour.');

/* Question table insertion from DB Engine */
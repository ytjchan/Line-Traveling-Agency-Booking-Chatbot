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
INSERT INTO TourGuide (LineID,Name) VALUES ('Group member A Line ID','Amber');
INSERT INTO TourGuide (LineID,Name) VALUES ('Group members B Line ID','Betty');
INSERT INTO TourGuide (LineID,Name) VALUES ('no need for Carol','Carol');
INSERT INTO TourGuide (LineID,Name) VALUES ('no need for Linsay','Linsay');

/* TourOffering table from "Booking Table" */

/* Booker table from "Customer Table" */

/* Booking table from "Customer Table" */

/* FAQ table from faq.md */
INSERT INTO FAQ (Keywoed,Question,Answer) VALUES ('apply', 'How to apply?', 'Customers shall approach the company by phone or visit our store (in Clearwater bay) with the choosen tour code and departure date. If it is not full, customers will be advised by the staff to pay the tour fee. Tour fee is non refundable. Customer can pay their fee by ATM to 123-345-432-211 of ABC Bank or by cash in our store. Customer shall send their pay-in slip to us by email or LINE.');
INSERT INTO FAQ (Keywoed,Question,Answer) VALUES ('gathering', 'Where is the gathering/assemble and dismiss spot?', 'We gather at the gathering spot "Exit A, Futian port, Shenzhen" at 8:00AM on the departure date. We dismiss at the same spot after the tour.');
INSERT INTO FAQ (Keywoed,Question,Answer) VALUES ('assemble', 'Where is the gathering/assemble and dismiss spot?', 'We gather at the gathering spot "Exit A, Futian port, Shenzhen" at 8:00AM on the departure date. We dismiss at the same spot after the tour.');
INSERT INTO FAQ (Keywoed,Question,Answer) VALUES ('dismiss', 'Where is the gathering/assemble and dismiss spot?', 'We gather at the gathering spot "Exit A, Futian port, Shenzhen" at 8:00AM on the departure date. We dismiss at the same spot after the tour.');
INSERT INTO FAQ (Keywoed,Question,Answer) VALUES ('cancel', 'What if the tour is cancelled?', 'In case a tour has not enough people or bad weather condition and the tour is forced to cancel, customers will be informed 3 days in advanced. Either change to another tour or refund is avaliable for customers to select. However, due to other reasons such as customers‘ personal problem no refund can be made.');
INSERT INTO FAQ (Keywoed,Question,Answer) VALUES ('charge', 'Are there any additional charge?', 'Each customer need to pay an additional service charge at the rate $60/day/person, on top of the tour fee. It is collected by the tour guide at the end of the tour.');
INSERT INTO FAQ (Keywoed,Question,Answer) VALUES ('transportation', 'What is the transportation in Guangdong?', 'A tour bus.');
INSERT INTO FAQ (Keywoed,Question,Answer) VALUES ('contact', 'How can I contact the tour guide?', 'Each tour guide has a LINE account and he will add the customers as his friends before the departure date. You can contact him/her accordingly.');
INSERT INTO FAQ (Keywoed,Question,Answer) VALUES ('tout guide', 'How can I contact the tour guide?', 'Each tour guide has a LINE account and he will add the customers as his friends before the departure date. You can contact him/her accordingly.');
INSERT INTO FAQ (Keywoed,Question,Answer) VALUES ('insurance', 'Is insurance covered?', 'Yes, each customers are protected by the Excellent Blue-Diamond Scheme by AAA insurance company.');
INSERT INTO FAQ (Keywoed,Question,Answer) VALUES ('hotel', 'Is the hotel single bed or twin bed or double bed?', 'All rooms are twin beds. Every customer needs to share a room with another customer. If a customer prefer to own a room by himself/herself, additional charge of 30% will be applied.');
INSERT INTO FAQ (Keywoed,Question,Answer) VALUES ('room', 'Is the hotel single bed or twin bed or double bed?', 'All rooms are twin beds. Every customer needs to share a room with another customer. If a customer prefer to own a room by himself/herself, additional charge of 30% will be applied.');
INSERT INTO FAQ (Keywoed,Question,Answer) VALUES ('bed', 'Is the hotel single bed or twin bed or double bed?', 'All rooms are twin beds. Every customer needs to share a room with another customer. If a customer prefer to own a room by himself/herself, additional charge of 30% will be applied.');
INSERT INTO FAQ (Keywoed,Question,Answer) VALUES ('visa', 'Visa problem', 'Please refer the Visa issue to the immigration department of China. The tour are assembled and dismissed in mainland and no cross-border is needed. However, you will need a travelling document when you check in the hotel.');
INSERT INTO FAQ (Keywoed,Question,Answer) VALUES ('swimming suit', 'Do I need swimming suit in a water them park or a hot spring resort?', 'Yes you do need it. Otherwise you may not use the facility.');
INSERT INTO FAQ (Keywoed,Question,Answer) VALUES ('vegeterian', 'Do you serve vegeterian?', 'No');
INSERT INTO FAQ (Keywoed,Question,Answer) VALUES ('fee', 'What is the tour fee for children?', 'Age below 3 (including 3) is free. Age between 4 to 11 (including 4 and 11) has a discount of 20% off. Otherwise full fee applies. The same service charge is applied to all age customers');
INSERT INTO FAQ (Keywoed,Question,Answer) VALUES ('children', 'What is the tour fee for children?', 'Age below 3 (including 3) is free. Age between 4 to 11 (including 4 and 11) has a discount of 20% off. Otherwise full fee applies. The same service charge is applied to all age customers');
INSERT INTO FAQ (Keywoed,Question,Answer) VALUES ('late', 'What if I am late in the departure date?', 'You shall contact the tour guide if you know you will be late and see if the tour guide can wait a little bit longer. No refund or make up shall be made if a customer is late for the tour.');

/* Question table insertion from DB Engine */
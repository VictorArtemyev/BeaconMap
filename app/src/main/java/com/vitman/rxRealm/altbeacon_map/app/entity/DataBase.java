package com.vitman.rxRealm.altbeacon_map.app.entity;

import android.graphics.PointF;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Victor Artemjev on 18.05.2015.
 */
public class DataBase {
    private static final DataBase INSTANCE = new DataBase();
    private static ClubFloor sFloor;
    private static List<ClubRoom> sClubRooms;
    private static Map<Integer, PandaBeacon> sPandaBeacons;
    private static List<Customer> sCustomers;

    private DataBase() {
        sClubRooms = new ArrayList<>();
        sPandaBeacons = new HashMap<>();
        sCustomers = new ArrayList<>();
        initMapFloor();
    }

    public static DataBase getInstance() {
        if (INSTANCE == null) {
            return new DataBase();
        }
        return INSTANCE;
    }

    private void initMapFloor() {
        // Devabit map
        sFloor = new ClubFloor();
        sFloor.setMapScale(100);
        sFloor.setWidth(1397);
        sFloor.setHeight(1327);

        // ROOM 1

        ClubRoom room1 = new ClubRoom();
        room1.setIdent(1);
        room1.getPoints().add(new PointF(6.44f, 1.70f));
        room1.getPoints().add(new PointF(13.56f, 0.50f));
        room1.getPoints().add(new PointF(6.44f, 13.00f));
        room1.getPoints().add(new PointF(13.56f, 13.00f));
        sClubRooms.add(room1);

        PandaBeacon pandaBeacon1Room1 = new PandaBeacon();
        pandaBeacon1Room1.setName("Beacon1");
        pandaBeacon1Room1.setX(10.00);
        pandaBeacon1Room1.setY(1.07);
        pandaBeacon1Room1.setStrenght(3);
        pandaBeacon1Room1.setClubRoom(room1);
        pandaBeacon1Room1.setMajor(101);
        sPandaBeacons.put(pandaBeacon1Room1.getMajor(), pandaBeacon1Room1);

        PandaBeacon pandaBeacon2Room1 = new PandaBeacon();
        pandaBeacon2Room1.setName("Beacon2");
        pandaBeacon2Room1.setX(10.00);
        pandaBeacon2Room1.setY(7.03);
        pandaBeacon2Room1.setStrenght(3);
        pandaBeacon2Room1.setClubRoom(room1);
        pandaBeacon2Room1.setMajor(102);
        sPandaBeacons.put(pandaBeacon2Room1.getMajor(), pandaBeacon2Room1);

        PandaBeacon pandaBeacon3Room1 = new PandaBeacon();
        pandaBeacon3Room1.setName("Beacon3");
        pandaBeacon3Room1.setX(10.00);
        pandaBeacon3Room1.setY(13.00);
        pandaBeacon3Room1.setStrenght(3);
        pandaBeacon3Room1.setClubRoom(room1);
        pandaBeacon3Room1.setMajor(103);
        sPandaBeacons.put(pandaBeacon3Room1.getMajor(), pandaBeacon3Room1);

        //ROOM 2

        ClubRoom room2 = new ClubRoom();
        room2.setIdent(2);
        room2.getPoints().add(new PointF(0.45f, 2.70f));
        room2.getPoints().add(new PointF(6.32f, 1.70f));
        room2.getPoints().add(new PointF(6.32f, 8.83f));
        room2.getPoints().add(new PointF(0.45f, 8.83f));
        sClubRooms.add(room2);

        PandaBeacon pandaBeacon1Room2 = new PandaBeacon();
        pandaBeacon1Room2.setName("Beacon1_Room2");
        pandaBeacon1Room2.setX(3.38);
        pandaBeacon1Room2.setY(3.00);
        pandaBeacon1Room2.setStrenght(3);
        pandaBeacon1Room2.setClubRoom(room2);
        pandaBeacon1Room2.setMajor(106);
        sPandaBeacons.put(pandaBeacon1Room2.getMajor(), pandaBeacon1Room2);

        PandaBeacon pandaBeacon2Room2 = new PandaBeacon();
        pandaBeacon2Room2.setName("Beacon1_Room2");
        pandaBeacon2Room2.setX(3.38);
        pandaBeacon2Room2.setY(8.83);
        pandaBeacon2Room2.setStrenght(3);
        pandaBeacon2Room2.setClubRoom(room2);
        pandaBeacon2Room2.setMajor(107);
        sPandaBeacons.put(pandaBeacon2Room2.getMajor(), pandaBeacon2Room2);

//        // Test map
//        sFloor = new ClubFloor();
//        sFloor.setMapScale(100);
//        sFloor.setWidth(1243);
//        sFloor.setHeight(1338);
//
//        // ROOM 1
//
//        ClubRoom room1 = new ClubRoom();
//        room1.setIdent(1);
//        room1.getPoints().add(new PointF(0.16f, 0.48f));
//        room1.getPoints().add(new PointF(5.79f, 0.48f));
//        room1.getPoints().add(new PointF(0.16f, 12.41f));
//        room1.getPoints().add(new PointF(5.79f, 12.41f));
//        sClubRooms.add(room1);
//
//        PandaBeacon pandaBeacon1Room1 = new PandaBeacon();
//        pandaBeacon1Room1.setName("Beacon1");
//        pandaBeacon1Room1.setX(1.44);
//        pandaBeacon1Room1.setY(2.35);
//        pandaBeacon1Room1.setStrenght(1.5);
//        pandaBeacon1Room1.setClubRoom(room1);
//        pandaBeacon1Room1.setMajor(301);
//        sPandaBeacons.put(pandaBeacon1Room1.getMajor(), pandaBeacon1Room1);
//
//        PandaBeacon pandaBeacon2Room1 = new PandaBeacon();
//        pandaBeacon2Room1.setName("Beacon2");
//        pandaBeacon2Room1.setX(1.44);
//        pandaBeacon2Room1.setY(6.20);
//        pandaBeacon2Room1.setStrenght(1.5);
//        pandaBeacon2Room1.setClubRoom(room1);
//        pandaBeacon2Room1.setMajor(302);
//        sPandaBeacons.put(pandaBeacon2Room1.getMajor(), pandaBeacon2Room1);
//
//        PandaBeacon pandaBeacon3Room1 = new PandaBeacon();
//        pandaBeacon3Room1.setName("Beacon3");
//        pandaBeacon3Room1.setX(1.44);
//        pandaBeacon3Room1.setY(10.05);
//        pandaBeacon3Room1.setStrenght(1.5);
//        pandaBeacon3Room1.setClubRoom(room1);
//        pandaBeacon3Room1.setMajor(304);
//        sPandaBeacons.put(pandaBeacon3Room1.getMajor(), pandaBeacon3Room1);
//
//        //ROOM 2
//
//        ClubRoom room2 = new ClubRoom();
//        room2.setIdent(2);
//        room2.getPoints().add(new PointF(5.95f, 0.48f));
//        room2.getPoints().add(new PointF(12.07f, 0.48f));
//        room2.getPoints().add(new PointF(5.95f, 6.59f));
//        room2.getPoints().add(new PointF(12.07f, 6.59f));
//        sClubRooms.add(room2);
//
//        PandaBeacon pandaBeacon1Room2 = new PandaBeacon();
//        pandaBeacon1Room2.setName("Beacon1_Room2");
//        pandaBeacon1Room2.setX(9.58);
//        pandaBeacon1Room2.setY(3.00);
//        pandaBeacon1Room2.setStrenght(1.5);
//        pandaBeacon1Room2.setClubRoom(room2);
//        pandaBeacon1Room2.setMajor(401);
//        sPandaBeacons.put(pandaBeacon1Room2.getMajor(), pandaBeacon1Room2);
//
//        //ROOM 3
//
//        ClubRoom room3 = new ClubRoom();
//        room3.setIdent(3);
//        room3.getPoints().add(new PointF(5.95f, 6.74f));
//        room3.getPoints().add(new PointF(12.07f, 6.74f));
//        room3.getPoints().add(new PointF(5.95f, 12.42f));
//        room3.getPoints().add(new PointF(12.07f, 12.42f));
//        sClubRooms.add(room3);
//
//        PandaBeacon pandaBeacon1Room3 = new PandaBeacon();
//        pandaBeacon1Room3.setName("Beacon1_Room3");
//        pandaBeacon1Room3.setX(9.52);
//        pandaBeacon1Room3.setY(8.51);
//        pandaBeacon1Room3.setStrenght(1.5);
//        pandaBeacon1Room3.setClubRoom(room3);
//        pandaBeacon1Room3.setMajor(405);
//        sPandaBeacons.put(pandaBeacon1Room3.getMajor(), pandaBeacon1Room3);

        // Init club guests

        String avatarUrl_1 = "https://encrypted-tbn2.gstatic.com/images?q=tbn:ANd9GcQjtrtlg62fz7YWeYNZRWiZXhCRwZ0vAK7SIa7s-T_To6gFLmBSuQ";
        String avatarUrl_2 = "https://im3-tub-ua.yandex.net/i?id=45646326c2d6323a65b549110f190cd9&n=21";
        String avatarUrl_3 = "https://im0-tub-ua.yandex.net/i?id=dd98fd79ec3d47650008ab6627b6e6ab&n=21";
        String avatarUrl_4 = "https://im1-tub-ua.yandex.net/i?id=c265b7d479431304111a4aaf8947e209&n=21";
        String avatarUrl_5 = "https://im1-tub-ua.yandex.net/i?id=2952d9943177d5bef95cae036d11fd25&n=21";
        String avatarUrl_6 = "https://im3-tub-ua.yandex.net/i?id=b5fb2f761a331224ff04dcc4ae9733c1&n=21";
        String avatarUrl_7 = "https://im3-tub-ua.yandex.net/i?id=1f823bf056e48be4ea501e2cfb4971c1&n=21";
        String avatarUrl_8 = "https://im0-tub-ua.yandex.net/i?id=a739c684a3ad1e7f103bddaec8a55a2d&n=21";
        String avatarUrl_9 = "https://im1-tub-ua.yandex.net/i?id=cd405e01a5d45028d366cab015faf51f&n=21";
        String avatarUrl_10 = "https://pbs.twimg.com/media/CGkmwmHU8AAl1IC.png";
        String avatarUrl_11 = "https://im1-tub-ua.yandex.net/i?id=cc0c57c6ca42c064c1937acc3a9e1fb4&n=21";
        String avatarUrl_12 = "https://im0-tub-ua.yandex.net/i?id=1de3e61d23b25313f10aad34ef897eac&n=21";
        String avatarUrl_13 = "https://im2-tub-ua.yandex.net/i?id=6cabb579bfb106fe11de164841f81452&n=21";
        String avatarUrl_14 = "https://im1-tub-ua.yandex.net/i?id=b93ba4996efc9ed8e142bad3c94732cc&n=21";
        String avatarUrl_15 = "https://im2-tub-ua.yandex.net/i?id=bde0d0846a3c743d76842b82662896a8&n=21";
        String avatarUrl_16 = "https://im1-tub-ua.yandex.net/i?id=785b50d987c9f3943c7321017b3988c7&n=21";
        String avatarUrl_17 = "https://im3-tub-ua.yandex.net/i?id=6a339ffe0bb59a63dc6a5f6cd6d1d114&n=21";
        String avatarUrl_18 = "https://im2-tub-ua.yandex.net/i?id=57bc8135ce9ae9ee2a30a4d9a10f5a53&n=21";
        String avatarUrl_19 = "https://im2-tub-ua.yandex.net/i?id=24bf5f1017b2d19476817cc45e3327c8&n=21";
        String avatarUrl_20 = "https://im1-tub-ua.yandex.net/i?id=bfd410894d862c18d83a303450c8becb&n=21";
        String avatarUrl_21 = "https://im3-tub-ua.yandex.net/i?id=a00fba85d288ab0ed5dc6b891cb82f67&n=21";
        String avatarUrl_22 = "https://im0-tub-ua.yandex.net/i?id=dc6d39dbda36c570026589e14b7b4220&n=21";
        String avatarUrl_23 = "https://im2-tub-ua.yandex.net/i?id=0ae0274748883d3bad34712f9ede7d08&n=21";
        String avatarUrl_24 = "https://im1-tub-ua.yandex.net/i?id=8dff580293b6c0c2dde6e07afca71fce&n=21";
        String avatarUrl_25 = "https://im2-tub-ua.yandex.net/i?id=5597fe18237f24994b5f9ba2138a8c4b&n=21";


//        String avatarUrl_20 = "http://images4.alphacoders.com/118/118748.jpg";
//        String avatarUrl_21 = "http://vk.fotofab.ru/download/wallpaper/20120116/1024x768_13.jpg";
//        String avatarUrl_22 = "http://ica.su/img/picture/Dec/07/5268968157b8b43a2301bb725e934b71/1.jpg";
//        String avatarUrl_23 = "http://grandwallpapers.net/photo/hram-v-provintsii-unnan-1024x768.jpg";
//        String avatarUrl_24 = "http://mariavgorskaya.35photo.ru/photos/20140122/656296.jpg";
//        String avatarUrl_25 = "http://es.best-wallpaper.net/wallpaper/1024x768/1205/3D-nature-scenery_1024x768.jpg";


        // 301
        Customer customer1 = new Customer(avatarUrl_1, 101, "fc0eef2e-aef3-44c5-84dd-f26823dbd0bd");
        Customer customer2 = new Customer(avatarUrl_2, 101, "8264da26-5b10-4dd8-aa58-98a45dea6985");
        Customer customer3 = new Customer(avatarUrl_3, 101, "696f79b4-b068-455e-a6ea-7b22c5210fb0");
        Customer customer4 = new Customer(avatarUrl_4, 101, "a0528ed4-c3b7-44d1-974c-aad41184d67d");
        Customer customer5 = new Customer(avatarUrl_5, 101, "483f414e-4572-4937-ad5a-38f3b337d2e3");

        //302
        Customer customer6 = new Customer(avatarUrl_6, 102, "483f414e-4572-4937-ad5a-38f3b337d2e5");
        Customer customer7 = new Customer(avatarUrl_7, 102, "483f414e-4572-4937-ad5a-38f3b337d2e4");
        Customer customer8 = new Customer(avatarUrl_8, 102, "483f414e-4572-4937-ad5a-38f3b337d2e8");
        Customer customer9 = new Customer(avatarUrl_9, 102, "483f414e-4572-4937-ad5a-38f3b337d2e7");
        Customer customer10 = new Customer(avatarUrl_25, 102, "483f414e-4572-4937-ad5");
        Customer customer11 = new Customer(avatarUrl_11, 102, "483f414e-4572-4937-");

        //304
        Customer customer12 = new Customer(avatarUrl_12, 103, "483f414e-4572-4937-ad5a-38f3b337");
        Customer customer13 = new Customer(avatarUrl_13, 103, "414e-4572-4937-ad5a-38f37d2e7");
        Customer customer14 = new Customer(avatarUrl_14, 103, "f414e-4572-4937-ad7d2e7");
        Customer customer15 = new Customer(avatarUrl_15, 103, "483f414e-4572-4937-ad5ad2e7");
        Customer customer16 = new Customer(avatarUrl_16, 103, "483f414e-4572-4937-ad5a-38fd2e7");
        Customer customer17 = new Customer(avatarUrl_17, 103, "483f414e-4572-4937-ad537d2e7");
        Customer customer18 = new Customer(avatarUrl_18, 103, "483f414e-4572-4937b337d2e7");

        //401
        Customer customer19 = new Customer(avatarUrl_19, 106, "f414e-4572-4937-ad5a-38f37d2e7");
        Customer customer20 = new Customer(avatarUrl_20, 106, "414e-42--ad5a-38f3b337d2e7");
        Customer customer21 = new Customer(avatarUrl_21, 106, "f414e-4572-4937-ad5a-38f3b337d2e7");
        Customer customer22 = new Customer(avatarUrl_22, 106, "483f414e-337d2e7");

        //404
        Customer customer23 = new Customer(avatarUrl_23, 107, "483f414e-4572-49377d2e7");
        Customer customer24 = new Customer(avatarUrl_24, 107, "-4572-4937-ad5a-38f3b337d2e7");
        Customer customer25 = new Customer(avatarUrl_25, 107, "483f414e-4572-4937-f3b337d2e7");
        Customer currentUser = new Customer(avatarUrl_10, 107, "current_user_id");

        sCustomers.add(customer1);
        sCustomers.add(customer2);
        sCustomers.add(customer3);
        sCustomers.add(customer4);
        sCustomers.add(customer5);
        sCustomers.add(customer6);
        sCustomers.add(customer7);
        sCustomers.add(customer8);
        sCustomers.add(customer9);
        sCustomers.add(customer10);
        sCustomers.add(customer11);
        sCustomers.add(customer12);
        sCustomers.add(customer13);
        sCustomers.add(customer14);
        sCustomers.add(customer15);
        sCustomers.add(customer16);
        sCustomers.add(customer17);
        sCustomers.add(customer18);
        sCustomers.add(customer19);
        sCustomers.add(customer20);
        sCustomers.add(customer21);
        sCustomers.add(customer22);
        sCustomers.add(customer23);
        sCustomers.add(customer24);
        sCustomers.add(customer25);

        sCustomers.add(currentUser);

    }

    public Map<Integer, PandaBeacon> getPandaBeacons() {
        return sPandaBeacons;
    }

    public List<ClubRoom> getRooms() {
        return sClubRooms;
    }

    public ClubFloor getFloor() {
        return sFloor;
    }

    public List<Customer> getCustomers() {
        return sCustomers;
    }

}

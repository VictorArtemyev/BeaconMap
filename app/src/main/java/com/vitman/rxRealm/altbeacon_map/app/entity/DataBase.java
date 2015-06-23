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
        String avatarUrl_2 = "http://s00.yaplakal.com/pics/userpic/7/0/0/av-309007.jpg";
        String avatarUrl_3 = "http://1.bp.blogspot.com/_TUAgZ4Z8MQg/TQkXeuBSZoI/AAAAAAAAAEU/sEJpUZDTEP8/s1600/success.jpg";
        String avatarUrl_4 = "http://fscomps.fotosearch.com/compc/CSP/CSP990/k8774040.jpg";
        String avatarUrl_5 = "http://s16.radikal.ru/i190/1104/e7/328231386bcb.jpg";
        String avatarUrl_6 = "http://ncuxo-ycnex.ru/wp-content/uploads/2011/04/razvitie_polozitelnogo_otnosheniya.jpg";
        String avatarUrl_7 = "http://static.kinokopilka.tv/system/images/users/avatars/003/378/314/Laska1987_original.jpg";
        String avatarUrl_8 = "http://lh5.googleusercontent.com/-KZbHPse7Lv8/AAAAAAAAAAI/AAAAAAAAAJA/ch7pFXlDbkQ/s512-c/photo.jpg";
        String avatarUrl_9 = "http://pl.wallpaperson.com/media/wallpapers/W1siZiIsIi9zeXN0ZW0vd2FsbHBhcGVyc19waG90b3MvMDAwLzA3OS8yMDAvb3JpZ2luYWwvd2FsbHBhcGVyc29uX2IyNl8yNV8uanBnIl0sWyJwIiwidGh1bWIiLCI2MDB4NjAwPiJdLFsiZSIsImpwZyJdXQ.jpg";
        String avatarUrl_10 = "http://teecraze.com/images/fury/glassesmakemesmart.jpg";
        String avatarUrl_11 = "http://i.artfile.ru/520x390_320492_[www.ArtFile.ru].jpg";
        String avatarUrl_12 = "http://img4i.spoki.tvnet.lv/upload/articles/38/384916/images/Wallpapers-11.jpg";
        String avatarUrl_13 = "http://egel.narod.ru/EGEL46.jpg";
        String avatarUrl_14 = "http://dl23.fotosklad.org.ua/20111031/908fa46f0f639fd2ad4fb541a2f85525.jpg";
        String avatarUrl_15 = "http://images.forwallpaper.com/files/thumbs/preview/67/678782__entropy-jack-skellington_p.jpg";
        String avatarUrl_16 = "http://2krota.ru/uploads/posts/2009-02/1235755895_p_656.jpg";
        String avatarUrl_17 = "http://lol54.ru/uploads/posts/2012-12/thumbs/1355417276_lol54.ru_belki_009.jpg";
        String avatarUrl_18 = "http://topchik.ru/profile/1388350fa88b5cb4ea.jpg?4444519";
        String avatarUrl_19 = "http://www.romanticcollection.ru/gallery/albums/angel/193a.jpg";
        String avatarUrl_20 = "http://hq-wall.net/i/med_thumb/10/69/Liv_Tyler_da8980c44dcf0d78d6d54aac55505a71.jpg";
        String avatarUrl_21 = "http://oboi.cc.fozzyhost.com/640-480-100-uploads/11_05_2013/view/201209/oboik.ru_26524.jpg";
        String avatarUrl_22 = "http://www.nudecelebs.ru/photos/big/Mischa_Barton/Galereya_Mischa_Barton_ot_24_sentyabrya_2011_2_1316728261/Mischa_Barton_Wallpa-16.jpg";
        String avatarUrl_23 = "http://intensive.pokupkith.ru/media/images/v3/201504/site287653/review4_image-1430017111-2429.jpg";
        String avatarUrl_24 = "http://99px.ru/sstorage/56/2013/01/image_560401130928193026440.jpg";
        String avatarUrl_25 = "http://static.tumblr.com/aqagh5s/heYmc6wu3/a55.png";

        // 301
        Customer customer1 = new Customer(avatarUrl_1, 101, "fc0eef2e-aef3-44c5-84dd-f26823dbd0bd");
        Customer customer2 = new Customer(avatarUrl_2, 101, "8264da26-5b10-4dd8-aa58-98a45dea6985");
        Customer customer3 = new Customer(avatarUrl_3, 101, "696f79b4-b068-455e-a6ea-7b22c5210fb0");
        Customer customer4 = new Customer(avatarUrl_4, 101, "a0528ed4-c3b7-44d1-974c-aad41184d67d");
        Customer customer5 = new Customer(avatarUrl_5, 101, "483f414e-4572-4937-ad5a-38f3b337d2e3");

        //302
        Customer customer6 = new Customer(avatarUrl_6, 103, "483f414e-4572-4937-ad5a-38f3b337d2e5");
        Customer customer7 = new Customer(avatarUrl_7, 103, "483f414e-4572-4937-ad5a-38f3b337d2e4");
        Customer customer8 = new Customer(avatarUrl_8, 103, "483f414e-4572-4937-ad5a-38f3b337d2e8");
        Customer customer9 = new Customer(avatarUrl_9, 103, "483f414e-4572-4937-ad5a-38f3b337d2e7");
        Customer customer10 = new Customer(avatarUrl_25, 103, "483f414e-4572-4937-ad5");
        Customer customer11 = new Customer(avatarUrl_11, 103, "483f414e-4572-4937-");

        //304
        Customer customer12 = new Customer(avatarUrl_12, 103, "483f414e-4572-4937-ad5a-38f3b337");
        Customer customer13 = new Customer(avatarUrl_13, 103, "414e-4572-4937-ad5a-38f37d2e7");
        Customer customer14 = new Customer(avatarUrl_14, 103, "f414e-4572-4937-ad7d2e7");
        Customer customer15 = new Customer(avatarUrl_15, 103, "483f414e-4572-4937-ad5ad2e7");
        Customer customer16 = new Customer(avatarUrl_16, 103, "483f414e-4572-4937-ad5a-38fd2e7");
        Customer customer17 = new Customer(avatarUrl_17, 103, "483f414e-4572-4937-ad537d2e7");
        Customer customer18 = new Customer(avatarUrl_18, 103, "483f414e-4572-4937b337d2e7");

        //401
        Customer customer19 = new Customer(avatarUrl_19, 103, "f414e-4572-4937-ad5a-38f37d2e7");
        Customer customer20 = new Customer(avatarUrl_20, 103, "414e-42--ad5a-38f3b337d2e7");
        Customer customer21 = new Customer(avatarUrl_21, 103, "f414e-4572-4937-ad5a-38f3b337d2e7");
        Customer customer22 = new Customer(avatarUrl_22, 103, "483f414e-337d2e7");

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

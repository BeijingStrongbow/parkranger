package com.github.beijingstrongbow.parkranger;


import com.google.firebase.database.*;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import android.os.Handler;

/**
 * Created by ericd on 4/21/2018.
 */

public class FirebaseHandler {
    private LocationHandler locationHandler = LocationHandler.getInstance();
    private FirebaseDatabase database;
    private DatabaseReference sos;
    private DatabaseReference groups;
    private DatabaseReference rangers;

    private User user;

    private ArrayList<User> locations = new ArrayList<User>();
    private ArrayList<SOS> flags = new ArrayList<SOS>();
    private ArrayList<String> groupIds = new ArrayList<String>();
    private ArrayList<User> rangerLocations = new ArrayList<User>();
    private HashMap<String, String> rangerLogins = new HashMap<String, String>();

    private static FirebaseHandler h = null;

    public static FirebaseHandler getInstance() {
        if(h == null) {
            h = new FirebaseHandler();
        }

        return h;
    }

    private FirebaseHandler() {
        database = FirebaseDatabase.getInstance();
        sos = database.getReference("sos");
        groups = database.getReference("groups");
        rangers = database.getReference("rangers");
    }

    private FirebaseHandler(String userId) {
        database = FirebaseDatabase.getInstance();
        sos = database.getReference("sos");
        groups = database.getReference("groups");
        rangers = database.getReference("rangers");
        user = new User();
        user.uuid = UUID.fromString(userId);
        user.isRanger = false;
    }

    /**
     * Put a new SOS in the database
     *
     * @param latitude The latitude of the SOS
     * @param longitude The longitude of the SOS
     */
    public void putSOS(double latitude, double longitude, String message) {
        DatabaseReference newSOS = sos.push();
        newSOS.child("latitude").setValue(Double.toString(latitude));
        newSOS.child("longitude").setValue(Double.toString(longitude));
        newSOS.child("message").setValue(message);
        newSOS.child("time").setValue(System.currentTimeMillis());
        newSOS.child("snippet").setValue("");
    }

    /**
     * Adds a group with the specified group id
     *
     * @param groupId Group ID to create
     * @param creatorLat The creator's latitude
     * @param creatorLong The creator's longitude
     */
    public boolean addGroup(int groupId, double creatorLat, double creatorLong, String name) {
        if(groupIds.contains(Integer.toString(groupId))) {
            return false;
        }

        UUID uuid = UUID.randomUUID();
        DatabaseReference newGroup = groups.push();
        newGroup.child("id").setValue(Integer.toString(groupId));
        System.out.println("test1");

        DatabaseReference members = newGroup.child("members");
        DatabaseReference newMember = members.child(uuid.toString());
        newMember.child("latitude").setValue(Double.toString(creatorLat));
        newMember.child("longitude").setValue(Double.toString(creatorLong));
        newMember.child("name").setValue(name);

        user = new User();
        user.uuid = uuid;
        user.latitude = creatorLat;
        user.longitude = creatorLong;
        user.name = name;
        user.groupId = groupId;
        user.isRanger = false;
        startUpdating(groupId);

        return true;
    }

    /**
     * Adds a user to a group if the group exists, otherwise does nothing
     *
     * @param groupId The id of the group to join
     * @param latitude The user's current latitude
     * @param longitude The user's current longitude
     */
    public boolean addUserToGroup(final int groupId, final double latitude, final double longitude, final String name) {
        final UUID user = UUID.randomUUID();

        if(!groupIds.contains(Integer.toString(groupId))) {
            return false;
        }

        groups.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot d : dataSnapshot.getChildren()) {
                    String id = (String) d.child("id").getValue();
                    System.out.println(id);
                    if(id.equals(Integer.toString(groupId))) {
                        DatabaseReference newMember = groups.child(d.getKey()).child("members").child(user.toString());
                        newMember.child("latitude").setValue(Double.toString(latitude));
                        newMember.child("longitude").setValue(Double.toString(longitude));
                        newMember.child("name").setValue(name);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
        this.user = new User();
        this.user.uuid = user;
        this.user.latitude = latitude;
        this.user.longitude = longitude;
        this.user.isRanger = false;
        this.user.groupId = groupId;
        this.user.name = name;
        startUpdating(groupId);
        return true;
    }

    public void logOut() {

    }

    public void startUpdating(final int groupId) {
        final Handler handler = new Handler();

        Runnable update = new Runnable() {
            @Override
            public void run() {
                groups.addListenerForSingleValueEvent(new ValueEventListener() {
                    ArrayList<User> temp = new ArrayList<User>();

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for(DataSnapshot group : dataSnapshot.getChildren()) {
                            String id = (String) group.child("id").getValue();
                            System.out.println(user.isRanger);
                            if(user.isRanger || id.equals(Integer.toString(groupId))) {

                                for(DataSnapshot member : group.child("members").getChildren()) {
                                    if(user.uuid != null && ((String) member.getKey()).equals(user.uuid.toString())) {
                                        groups.child(group.getKey()).child("members").child(member.getKey()).child("latitude").setValue(Double.toString(locationHandler.getLatitude()));
                                        groups.child(group.getKey()).child("members").child(member.getKey()).child("longitude").setValue(Double.toString(locationHandler.getLongitude()));
                                    }
                                    else {
                                        User loc = new User();
                                        loc.latitude = Double.parseDouble((String) member.child("latitude").getValue());
                                        loc.longitude = Double.parseDouble((String) member.child("longitude").getValue());
                                        loc.name = (String) member.child("name").getValue();
                                        temp.add(loc);
                                    }
                                }
                            }
                        }
                        locations = temp;
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {}
                });

                sos.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        ArrayList<SOS> flagTemp = new ArrayList<SOS>();

                        for(DataSnapshot d : dataSnapshot.getChildren()) {
                            SOS loc = new SOS();
                            loc.latitude = Double.parseDouble((String) d.child("latitude").getValue());
                            loc.longitude = Double.parseDouble((String) d.child("longitude").getValue());
                            loc.message = (String) d.child("message").getValue();
                            loc.snippet = (String) d.child("snippet").getValue();
                            flagTemp.add(loc);
                        }
                        flags = flagTemp;
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {}
                });

                if(user.isRanger) {
                    rangers.addListenerForSingleValueEvent(new ValueEventListener() {
                        ArrayList<User> temp = new ArrayList<User>();

                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for(DataSnapshot d : dataSnapshot.getChildren()) {
                                if(d.getKey().equals(user.name)) {
                                    rangers.child(d.getKey()).child("latitude").setValue(Double.toString(locationHandler.getLatitude()));
                                    rangers.child(d.getKey()).child("longitude").setValue(Double.toString(locationHandler.getLongitude()));
                                }
                                else {
                                    User loc = new User();
                                    loc.latitude = Double.parseDouble((String) d.child("latitude").getValue());
                                    loc.longitude = Double.parseDouble((String) d.child("longitude").getValue());
                                    loc.name = (String) d.getKey();
                                    loc.isRanger = true;
                                    temp.add(loc);
                                }
                            }
                            rangerLocations = temp;
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {}
                    });
                }

                handler.postDelayed(this, 5000);
            }
        };

        handler.postDelayed(update, 5000);
    }

    public void getGroups() {
        groups.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot d : dataSnapshot.getChildren()) {
                    groupIds.add((String) d.child("id").getValue());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
        rangers.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot d : dataSnapshot.getChildren()) {
                    rangerLogins.put(d.getKey(), ((String) d.child("password").getValue()));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    public boolean checkLogin(String username, String password) {
        if(rangerLogins.containsKey(username) && rangerLogins.get(username).equals(password)) {
            user = new User();
            user.isRanger = true;
            user.name = username;
            user.latitude = locationHandler.getLatitude();
            user.longitude = locationHandler.getLongitude();
            startUpdating(1234);
            return true;
        }
        return false;
    }

    public void handleSOS(final String message) {
        if(user.isRanger) {
            sos.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for(DataSnapshot data : dataSnapshot.getChildren()) {
                        if(((String) data.child("message").getValue()).equals(message)) {
                            sos.child(data.getKey()).child("snippet").setValue(user.name + " is coming");
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {}
            });
        }
    }

    public ArrayList<User> getLocations() {
        return locations;
    }
    public ArrayList<User> getRangerLocations() {
        if(user.isRanger) {
            return rangerLocations;
        }
        else {
            return new ArrayList<User>();
        }
    }
    public String getName() { return user.name;}
    public String getGroupID() { return Integer.toString(user.groupId);}
    public ArrayList<SOS> getSOS() { return flags; }
}

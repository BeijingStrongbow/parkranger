package com.github.beijingstrongbow.parkranger;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.UUID;

import android.os.Handler;

/**
 * Created by ericd on 4/21/2018.
 */

public class FirebaseHandler {

    private FirebaseDatabase database;
    private DatabaseReference sos;
    private DatabaseReference groups;

    private double latitude = 56.2;
    private double longitude = 37.8;

    private UUID user;

    private ArrayList<User> locations = new ArrayList<User>();
    private ArrayList<SOS> flags = new ArrayList<SOS>();

    public FirebaseHandler() {
        database = FirebaseDatabase.getInstance();
        sos = database.getReference("sos");
        groups = database.getReference("groups");
    }

    public FirebaseHandler(String userId) {
        database = FirebaseDatabase.getInstance();
        sos = database.getReference("sos");
        groups = database.getReference("groups");
        user = UUID.fromString(userId);
    }

    /**
     * Put a new SOS in the database
     *
     * @param latitude The latitude of the SOS
     * @param longitude The longitude of the SOS
     */
    public void putSOS(double latitude, double longitude) {
        DatabaseReference newSOS = sos.push();
        newSOS.child("latitude").setValue(Double.toString(latitude));
        newSOS.child("longitude").setValue(Double.toString(longitude));
        newSOS.child("time").setValue(System.currentTimeMillis());
    }

    /**
     * Adds a group with the specified group id
     *
     * @param groupId Group ID to create
     * @param creatorLat The creator's latitude
     * @param creatorLong The creator's longitude
     */
    public void addGroup(int groupId, double creatorLat, double creatorLong, String name) {
        UUID uuid = UUID.randomUUID();
        DatabaseReference newGroup = groups.push();
        newGroup.child("id").setValue(Integer.toString(groupId));

        DatabaseReference members = newGroup.child("members");
        DatabaseReference newMember = members.child(uuid.toString());
        newMember.child("latitude").setValue(Double.toString(creatorLat));
        newMember.child("longitude").setValue(Double.toString(creatorLong));
        newMember.child("name").setValue(name);

        user = uuid;
        startUpdating(groupId);
    }

    /**
     * Adds a user to a group if the group exists, otherwise does nothing
     *
     * @param groupId The id of the group to join
     * @param latitude The user's current latitude
     * @param longitude The user's current longitude
     */
    public void addUserToGroup(final int groupId, final double latitude, final double longitude, final String name) {
        final UUID user = UUID.randomUUID();

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
        this.user = user;
        startUpdating(groupId);
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

                            if(id.equals(Integer.toString(groupId))) {

                                for(DataSnapshot member : group.child("members").getChildren()) {
                                    if(((String) member.getKey()).equals(user.toString())) {
                                        groups.child(group.getKey()).child("members").child(member.getKey()).child("latitude").setValue(Double.toString(latitude));
                                        groups.child(group.getKey()).child("members").child(member.getKey()).child("longitude").setValue(Double.toString(longitude));
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
                            flagTemp.add(loc);
                        }
                        flags = flagTemp;
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {}
                });

                handler.postDelayed(this, 5000);
            }
        };

        handler.postDelayed(update, 5000);
    }

    public void setLocation(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public ArrayList<User> getLocations() {
        return locations;
    }

    public ArrayList<SOS> getSOS() {
        return flags;
    }
}

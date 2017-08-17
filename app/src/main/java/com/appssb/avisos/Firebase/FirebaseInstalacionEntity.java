package com.appssb.avisos.Firebase;



/**
 * Created by Nenfis on 01/08/2017.
 */

public class FirebaseInstalacionEntity {
    private String uid;
    private String sector;
    private String instalacion;

    public FirebaseInstalacionEntity(){
    }




    public FirebaseInstalacionEntity(String uid, String sector, String instalacion){
        this.uid=uid;
        this.sector=sector;
        this.instalacion=instalacion;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getInstalacion() {
        return instalacion;
    }

    public void setInstalacion(String instalacion) {
        this.instalacion = instalacion;
    }

    public String getSector() {
        return sector;
    }

    public void setSector(String sector) {
        this.sector = sector;
    }

    @Override
    public String toString() {
        return instalacion;
    }
}

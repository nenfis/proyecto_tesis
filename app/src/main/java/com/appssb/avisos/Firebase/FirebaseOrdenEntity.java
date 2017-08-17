package com.appssb.avisos.Firebase;

/**
 * Created by Nenfis on 01/08/2017.
 */

public class FirebaseOrdenEntity {
    private String id;
    private String estado;
    private String date;
    private String planificador;
    private String ejecutor;
    private String instalacion;
    private String descripcion;
    private String reporte;
    private String date2;


    public FirebaseOrdenEntity(){
    }




    public FirebaseOrdenEntity(String id, String estado, String date, String planificador, String ejecutor, String instalacion, String descripcion, String reporte, String date2) {
        this.id = id;
        this.estado=estado;
        this.date = date;
        this.planificador=planificador;
        this.ejecutor=ejecutor;
        this.instalacion= instalacion;
        this.descripcion=descripcion;
        this.reporte=reporte;
        this.date2=date2;

    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPlanificador() {
        return planificador;
    }

    public void setPlanificador(String planificador) {
        this.planificador = planificador;
    }

    public String getEjecutor() {
        return ejecutor;
    }

    public void setEjecutor(String ejecutor) {
        this.ejecutor = ejecutor;
    }

    public String getInstalacion() {
        return instalacion;
    }

    public void setInstalacion(String instalacion) {
        this.instalacion = instalacion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getReporte() {
        return reporte;
    }

    public void setReporte(String reporte) {
        this.reporte = reporte;
    }

    public String getDate2() {
        return date2;
    }

    public void setDate2(String date2) {
        this.date2 = date2;
    }


    @Override
    public String toString() {
        return "Estado= " + estado + '\n' +
                "Planificador= " + planificador + '\n' +
                "Ejecutor= " + ejecutor + '\n' +
                "Instalación= " + instalacion + '\n'
                ;
    }

}

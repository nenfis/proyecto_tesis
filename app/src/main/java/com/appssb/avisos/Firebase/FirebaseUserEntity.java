package com.appssb.avisos.Firebase;


public class FirebaseUserEntity {

    private String uId;

    private String email;

    private String pass;

    private String name;

    private String tipo;



    public FirebaseUserEntity(){
    }

    public FirebaseUserEntity(String uId, String email, String pass, String name,  String tipo) {
        this.uId = uId;
        this.email = email;
        this.pass=pass;
        this.name = name;
        this.tipo = tipo;

    }



    public String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    @Override
    public String toString() {
        return "Email= " + email + '\n' +
                "Nombre= " + name + '\n' +
                "Tipo de usuario= " + tipo + '\n';

    }
}

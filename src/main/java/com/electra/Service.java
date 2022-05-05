package com.electra;

import java.sql.*;
import java.util.ArrayList;

@org.springframework.stereotype.Service
public class Service {


    public void addAngajat(Person angajat ) throws SQLException {
        Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/Electra", "root", "parola06");
        con.setAutoCommit(false);
        int idInsertAngajat;
        try {
            PreparedStatement checkDuplicateAngajati = con.prepareStatement("select * from Angajati where Nume=? and Prenume=? and Salariu=?");
            checkDuplicateAngajati.setString(1, angajat.getNume());
            checkDuplicateAngajati.setString(2, angajat.getPrenume());
            checkDuplicateAngajati.setDouble(3, angajat.getSalariu());
            checkDuplicateAngajati.executeQuery();
            ResultSet rs = checkDuplicateAngajati.executeQuery();

            if (!rs.next()) {
                PreparedStatement addAngajat = con.prepareStatement("insert into Angajati(Nume,Prenume,Salariu) values (?,?,?)");
                addAngajat.setString(1,angajat.getNume());
                addAngajat.setString(2, angajat.getPrenume());
                addAngajat.setDouble(3, angajat.getSalariu());
                addAngajat.executeUpdate();
            }

            PreparedStatement getID = con.prepareStatement("select * from Angajati where Nume=? and Prenume=?");
            getID.setString(1, angajat.getNume());
            getID.setString(2, angajat.getPrenume());
            ResultSet rs1 = getID.executeQuery();
            while (rs1.next()) {
                idInsertAngajat = rs1.getInt("ID_Angajat");
                PreparedStatement checkDuplicateFunctie = con.prepareStatement("select * from Functie where ID_Angajat = ?");
                checkDuplicateFunctie.setInt(1, idInsertAngajat);
                ResultSet rsFunctie = checkDuplicateFunctie.executeQuery();
                if(!rsFunctie.next()) {
                    PreparedStatement addFunctie = con.prepareStatement("insert into Functie values (?,?,?)");
                    addFunctie.setInt(1, idInsertAngajat);
                    addFunctie.setString(2, angajat.getFunctie().getNumeFunctie());
                    addFunctie.setInt(3, angajat.getFunctie().getNivel());
                    addFunctie.executeUpdate();
                }
            }
            con.commit();
            con.close();
        }catch(SQLException e){
            e.printStackTrace();
            con.rollback();
        }
    }

    public String concediezAngajat() throws SQLException {
        Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/Electra", "root", "parola06");
        PreparedStatement ps = con.prepareStatement("select * from Functie");
        ResultSet rs = ps.executeQuery();
        while(rs.next()){
            int nivel = rs.getInt("Nivel");
            int id = rs.getInt("ID_Angajat");
            if(nivel < 5){
                PreparedStatement ps2 = con.prepareStatement("delete from Functie where Nivel = ?");
                ps2.setInt(1, nivel);
                ps2.executeUpdate();
                PreparedStatement ps3 = con.prepareStatement("delete from Angajati where ID_Angajat = ?");
                ps3.setInt(1, id);
                ps3.executeUpdate();
            }
        }
        return "Vacanta Placuta!";
    }

    public ArrayList<Person> getList () throws SQLException {
        Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/Electra", "root", "parola06");
        ArrayList<Person> list = new ArrayList<>();
        PreparedStatement ps = con.prepareStatement("select * from Angajati inner join Functie on Angajati.id_angajat = Functie.id_angajat");
        ResultSet rs = ps.executeQuery();
        while(rs.next()){
            Person angajat = new Person();
            Functie functie = new Functie();
            String nume = rs.getString("Nume");
            String prenume = rs.getString("Prenume");
            double salariu = rs.getDouble("Salariu");
            String numeFunctie = rs.getString("Nume_functie");
            int nivel = rs.getInt("Nivel");
            functie.setNumeFunctie(numeFunctie);
            functie.setNivel(nivel);
            angajat.setNume(nume);
            angajat.setPrenume(prenume);
            angajat.setSalariu(salariu);
            angajat.setFunctie(functie);
            list.add(angajat);
        }
        return list;
    }

    public String marireSalariu() throws SQLException {
        Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/Electra", "root", "parola06");
        PreparedStatement ps = con.prepareStatement("Select * from Functie");
        ResultSet rs = ps.executeQuery();
        while (rs.next()){
            int id = rs.getInt("ID_Angajat");
            int nivel = rs.getInt("Nivel");
            if(nivel >= 9) {
                PreparedStatement ps2 = con.prepareStatement("update Angajati set Salariu = Salariu + 1000 where ID_Angajat = ?");
                ps2.setInt(1, id);
                ps2.executeUpdate();
            }
        }
        return "Marire salariu cu succes celor cu nivelul peste 9, inclusiv!";
    }

    public ArrayList<String> startWork() throws SQLException {
        Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/Electra", "root", "parola06");
        ArrayList<Person> listAngajati = new ArrayList<>();
        ArrayList<String> listaStartWorking = new ArrayList<>();
        PreparedStatement ps = con.prepareStatement("select * from Angajati inner join Functie on Angajati.id_angajat = Functie.id_angajat");
        ResultSet rs = ps.executeQuery();
        String str;
        while(rs.next()) {
            Person angajat = new Person();
            Functie functie = new Functie();
            String nume = rs.getString("Nume");
            String prenume = rs.getString("Prenume");
            double salariu = rs.getDouble("Salariu");
            String numeFunctie = rs.getString("Nume_functie");
            int nivel = rs.getInt("Nivel");
            functie.setNumeFunctie(numeFunctie);
            functie.setNivel(nivel);
            angajat.setNume(nume);
            angajat.setPrenume(prenume);
            angajat.setSalariu(salariu);
            angajat.setFunctie(functie);
            listAngajati.add(angajat);
        }
        for (Person person : listAngajati) {
            str = person.getPrenume() + ", lucrez ca " + person.getFunctie().getNumeFunctie();
            listaStartWorking.add(str);
        }
        return listaStartWorking;
    }

    public String stopWorkingAs(String nume, String prenume, String updateNumeFunctie) throws SQLException {
        Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/Electra", "root", "parola06");
        PreparedStatement ps = con.prepareStatement("select * from Angajati inner join Functie on Angajati.id_angajat = Functie.id_angajat where Nume = ? and Prenume = ?");
        ps.setString(1, nume);
        ps.setString(2, prenume);
        ResultSet rs = ps.executeQuery();
        String numeFunctieActuala = "";
        int id;
        while (rs.next()){
            numeFunctieActuala = rs.getString("Nume_functie");
            id = rs.getInt("ID_Angajat");
            PreparedStatement ps2 = con.prepareStatement("update Functie set Nume_functie = ? where ID_Angajat = ?");
            ps2.setString(1, updateNumeFunctie);
            ps2.setInt(2, id);
            if(updateNumeFunctie.equals(numeFunctieActuala)){
                return "Angajatul are deja acesta functie in CV";
            }else{
                ps2.executeUpdate();
            }
        }
        return "Angajatul " + nume + " " + prenume + " nu mai lucreaza ca " + numeFunctieActuala + " , aci lucreaza ca " + " " + updateNumeFunctie;
    }
}

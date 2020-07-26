package wwsis.caveatemptorc;



import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.sql.*;


class Okno extends JFrame {
    JdbcConnect jdbcconnect = new JdbcConnect();
    String[] typyStatus = {"oczekuje", "wyslane", "zaplacone"};
    String[] typyKsiazki = {"sensacja", "kryminał", "fantastyka", "thriller", "horror", "obyczajowa", "poradnik", "biografia", "historyczna", "podróże", "romans", "popularnonaukowa", "młodzieżowa", "dziecięca", "reportaż", "podręcznik"};
    // dane do nawiązania komunikacji z bazą danych
    private PreparedStatement p_1;
    // pole na komunikaty od aplikacji
    private JTextField komunikat = new JTextField();
    // panel z zakładkami
    private JTabbedPane tp = new JTabbedPane();
    private JPanel p_kli = new JPanel(); // klienci
    private JPanel p_ksi = new JPanel(); // ksiązki
    private JPanel p_zam = new JPanel(); // zamówiemia
    // panel dla zarządzania klientami
    private JTextField pole_pesel = new JTextField();
    private JTextField pole_autor = new JTextField();
    private JTextField pole_isbn = new JTextField();
    private JTextField pole_tytul = new JTextField();
    private JComboBox lista_ksiazki = new JComboBox();
    private JComboBox lista_klienci = new JComboBox();
    private JComboBox lista_typ = new JComboBox(typyKsiazki);
    private JComboBox lista_status = new JComboBox(typyStatus);
    private JTextField pole_data_zam = new JTextField();
    private JTextField pole_wydawnictwo = new JTextField();
    private JTextField pole_rok = new JTextField();
    private JTextField pole_cena = new JTextField();
    private JTextField pole_im = new JTextField();
    private JTextField pole_naz = new JTextField();
    private JTextField pole_ur = new JTextField();
    private JTextField pole_mail = new JTextField();
    private JTextField pole_adr = new JTextField();
    private JTextField pole_tel = new JTextField();
    private JButton przyc_zapisz_kli = new JButton("zapisz");
    private JButton przyc_zapisz_zam = new JButton("zapisz");
    private JButton przyc_usun_kli = new JButton("usuń");
    private JButton przyc_zapisz_ksi = new JButton("zapisz");
    private JButton przyc_usun_ksi = new JButton("usuń");
    private JButton przyc_zmien_cene = new JButton("Zmień cene");
    private DefaultListModel<String> lmodel_kli = new DefaultListModel<>();
    private JList<String> l_kli = new JList<>(lmodel_kli);
    private JScrollPane sp_kli = new JScrollPane(l_kli);
    private DefaultListModel<String> lmodel_ksi = new DefaultListModel<>();
    private DefaultListModel<String> lmodel_zam = new DefaultListModel<>();
    private JList<String> l_ksi = new JList<>(lmodel_ksi);
    private JList<String> l_zam = new JList<>(lmodel_zam);
    private JScrollPane sp_ksi = new JScrollPane(l_ksi);
    private JScrollPane sp_zam = new JScrollPane(l_zam);
    // funkcja aktualizująca listę klientów
    private void AktualnaListaKlientów(JList<String> lis) {
        try (Connection conn=DriverManager.getConnection(jdbcconnect.jdbcUrl, jdbcconnect.jdbcUser, jdbcconnect.jdbcPass)) {
            Statement stmt = conn.createStatement();
            String sql = "SELECT klienci.pesel, nazwisko, imie, adres FROM klienci, kontakty WHERE klienci.pesel = kontakty.pesel ORDER BY nazwisko, imie";
            ResultSet res = stmt.executeQuery(sql);
            lmodel_kli.clear();
            while(res.next()) {
                String s = res.getString(1) + ": " + res.getString(2) + " " + res.getString(3) + ", " + res.getString(4);
                lmodel_kli.addElement(s);

            }
        }
        catch (SQLException ex) {
            komunikat.setText("nie udało się zaktualizować listy klientów");
        }
    }
    // delegat obsługujący zdarzenie akcji od przycisku 'zapisz klienta'
    private ActionListener akc_zap_kli = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent actionEvent) {

            String pesel = pole_pesel.getText();
            if ( !pesel.matches("[0-9]{3,11}") || pesel.length() != 11) {
                JOptionPane.showMessageDialog(Okno.this, "błąd w polu z peselem");
                pole_pesel.setText("");
                pole_pesel.requestFocus();
                return;
            }
            String imie = pole_im.getText().toUpperCase();
            if (! imie.matches("[[A-Za-zżźćńółęąśŻŹĆĄŚĘŁÓŃ]]+")) {
            	JOptionPane.showMessageDialog(Okno.this, "błąd w polu imię");
                pole_im.setText("");
                pole_im.requestFocus();
                return;
            }
            String nazwisko = pole_naz.getText().toUpperCase();
            if (! nazwisko.matches("[[A-Za-zżźćńółęąśŻŹĆĄŚĘŁÓŃ]]+")) {
            	JOptionPane.showMessageDialog(Okno.this, "błąd w polu nazwisko");
                pole_naz.setText("");
                pole_naz.requestFocus();
                return;
            }
            String ur = pole_ur.getText(); 
            if (!ur.matches("([12]\\d{3}-(0[1-9]|1[0-2])-(0[1-9]|[12]\\d|3[01]))")) {
                JOptionPane.showMessageDialog(Okno.this, "błąd w polu data urodzenia - wpisz w foramcie yyyy-mm-dd");
                pole_ur.setText("");
                pole_ur.requestFocus();
                return;
            }
            
            if (imie.equals("") || nazwisko.equals("") || ur.equals("")) {
                JOptionPane.showMessageDialog(Okno.this, "nie wypełnione pole z imieniem lub nazwiskiem lub datą urodzenia");
                return;
            }
            String mail = pole_mail.getText();
            
            if (!mail.matches("^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*$")) {
            	JOptionPane.showMessageDialog(Okno.this, "błąd w polu mail");
                pole_mail.setText("");
                pole_mail.requestFocus();
                return;
            }
            String adr = pole_adr.getText();
            String tel = pole_tel.getText();
            if (!tel.matches("(?:(?:(?:\\+|00)?48)|(?:\\(\\+?48\\)))?(?:1[2-8]|2[2-69]|3[2-49]|4[1-68]|5[0-9]|6[0-35-9]|[7-8][1-9]|9[145])\\d{7}")) {
            	JOptionPane.showMessageDialog(Okno.this, "błąd w polu telefon");
                pole_tel.setText("");
                pole_tel.requestFocus();
                return;
            }
            if (mail.equals("") || adr.equals("")) {
                JOptionPane.showMessageDialog(Okno.this, "nie wypełnione pole z emailem lub adresem");
                return;
            }
            try (Connection conn=DriverManager.getConnection(jdbcconnect.jdbcUrl, jdbcconnect.jdbcUser, jdbcconnect.jdbcPass)) {
                Statement stmt = conn.createStatement();
                String sql1 = "INSERT INTO klienci (pesel, imie, nazwisko, ur) VALUES('" + pesel + "', '" + imie + "', '" + nazwisko + "', '" + ur + "')";
                int res = stmt.executeUpdate(sql1);
                if (res == 1) {
                    komunikat.setText("OK - klient dodany do bazy");
                    String sql2 = "INSERT INTO kontakty (pesel, mail, adres, tel) VALUES('" + pesel + "', '" + mail + "', '" + adr + "', '" + tel + "')";
                    stmt.executeUpdate(sql2);
                    AktualnaListaKlientów(l_kli);
                }
            }
            catch(SQLException ex) {
            	String s = "błąd SQL - nie dodano clienta ";
            	komunikat.setText(s);
            	
            	s += ex.getMessage();
            	s += ex.getSQLState();
            	s += ex.getErrorCode();
            	System.out.println(s);
            }
        }
    };
    // delegat obsługujący zdarzenie akcji od przycisku 'usuń klienta'
    private ActionListener akc_usun_kli = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            if (l_kli.getSelectionModel().getSelectedItemsCount() == 0) return;
            String p = l_kli.getModel().getElementAt(l_kli.getSelectionModel().getMinSelectionIndex());
            p = p.substring(0, p.indexOf(':'));
            try (Connection conn=DriverManager.getConnection(jdbcconnect.jdbcUrl, jdbcconnect.jdbcUser, jdbcconnect.jdbcPass)) {
                Statement stmt = conn.createStatement();
                String sql = "SELECT COUNT(*) FROM zamowienia WHERE pesel = '" + p + "'";
                ResultSet res = stmt.executeQuery(sql);
                res.next();
                int k = res.getInt(1);
                if (k == 0) {
                    String sql1 = "DELETE FROM klienci WHERE pesel = '" + p + "'";
                    stmt.executeUpdate(sql1);
                    String sql2 = "DELETE FROM kontakty WHERE pesel = '" + p + "'";
                    stmt.executeUpdate(sql2);
                    komunikat.setText("OK - klient usunięty bazy");
                    AktualnaListaKlientów(l_kli);
                }
                else komunikat.setText("nie usunięto klienta, ponieważ składał już zamówienia");
            }
            catch (SQLException ex) {
            	String s = "błąd SQL - nie ununięto klienta";
            	s += ex.getMessage();
            	s += ex.getSQLState();
            	s += ex.getErrorCode();
                komunikat.setText(s);
            }
        }
    };

    private void AktualnaListaKsiazek(JList<String> lis) {
        try (Connection conn=DriverManager.getConnection(jdbcconnect.jdbcUrl, jdbcconnect.jdbcUser, jdbcconnect.jdbcPass)) {
            Statement stmt = conn.createStatement();
            String sql = "SELECT * FROM ksiazki";
            ResultSet res = stmt.executeQuery(sql);
            lmodel_ksi.clear();
            while(res.next()) {
                String s = res.getString(1) + ": " + res.getString(2) + " " + res.getString(3) + ", " + res.getString(4) + " " + res.getString(5) + " " + res.getString(6) + " " + res.getString(7);
                lmodel_ksi.addElement(s);

            }
        }
        catch (SQLException ex) {
            komunikat.setText("nie udało się zaktualizować listy klientów");
        }
    }
    // delegat obsługujący zdarzenie akcji od przycisku 'zapisz książke'
    private ActionListener akc_zap_ksi = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            String isbn = pole_isbn.getText();
            if (isbn.equals("")){
                JOptionPane.showMessageDialog(Okno.this, "nie wypełnione pole z Isbn");
                return;
            }
            String autor = pole_autor.getText();
            String tytul = pole_tytul.getText();
            String typ = (String) lista_typ.getSelectedItem();
            if (autor.equals("") || tytul.equals("")) {
                JOptionPane.showMessageDialog(Okno.this, "nie wypełnione pole z autorem lub tytułem");
                return;
            }
            String wydawnictwo = pole_wydawnictwo.getText();
            String rok = pole_rok.getText();
            String cena = pole_cena.getText();
            if (wydawnictwo.equals("") || rok.equals("")) {
                JOptionPane.showMessageDialog(Okno.this, "nie wypełnione pole z rokiem lub wydawnictwem");
                return;
            }
            try (Connection conn=DriverManager.getConnection(jdbcconnect.jdbcUrl, jdbcconnect.jdbcUser, jdbcconnect.jdbcPass)) {
                Statement stmt = conn.createStatement();
                String sql1 = "INSERT INTO ksiazki (isbn, autor, tytul, typ, wydawnictwo, rok, cena) VALUES('"+ pole_isbn.getText() + "', '" + pole_autor.getText() + "', '" + pole_tytul.getText() + "', '" + lista_typ.getSelectedItem() + "', '" + pole_wydawnictwo.getText()  + "', '" + pole_rok.getText() + "', '" + pole_cena.getText() + "')";
                int res = stmt.executeUpdate(sql1);
                if (res == 1) {
                    komunikat.setText("OK - klient dodany do bazy");
                    AktualnaListaKsiazek(l_ksi);
                }
            }
            catch(SQLException ex) {
                komunikat.setText("błąd SQL - nie zapisano klienta");
                System.out.println(ex);
            }
        }
    };

    // delegat obsługujący zdarzenie akcji od przycisku 'usuń książke'
    private ActionListener akc_usun_ksi = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            if (l_ksi.getSelectionModel().getSelectedItemsCount() == 0) return;
            String p = l_ksi.getModel().getElementAt(l_ksi.getSelectionModel().getMinSelectionIndex());
            p = p.substring(0, p.indexOf(':'));
            try (Connection conn=DriverManager.getConnection(jdbcconnect.jdbcUrl, jdbcconnect.jdbcUser, jdbcconnect.jdbcPass)) {
                Statement stmt = conn.createStatement();

                    String sql1 = "DELETE FROM ksiazki WHERE isbn = '" + p + "'";
                    stmt.executeUpdate(sql1);
                    komunikat.setText("OK - klient usunięty bazy");
                    AktualnaListaKsiazek(l_ksi);

            }
            catch (SQLException ex) {
                komunikat.setText("błąd SQL - nie ununięto klienta");
                System.out.println(ex);
            }
        }
    };
    // delegat obsługujący zdarzenie akcji od przycisku 'zmień cene'
    private ActionListener akc_zmien_cene = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent actionEvent) {

            try (Connection conn=DriverManager.getConnection(jdbcconnect.jdbcUrl, jdbcconnect.jdbcUser, jdbcconnect.jdbcPass)) {
                String isbn = pole_isbn.getText();
                String cena = pole_cena.getText();
                if (isbn.equals("") || cena.equals("") ){
                    JOptionPane.showMessageDialog(Okno.this, "nie wypełnione pole z Isbn lub cena");
                    return;
                }
                String sql1 = "UPDATE ksiazki SET cena =? WHERE isbn =?";
                PreparedStatement pstmt = conn.prepareStatement(sql1);
                pstmt.setString(1, pole_cena.getText());
                pstmt.setString(2, String.valueOf(Integer.parseInt(pole_isbn.getText())));
                pstmt.executeUpdate();
                komunikat.setText("OK - cena zmieniona");

                AktualnaListaKsiazek(l_ksi);

            }
            catch (SQLException ex) {
                komunikat.setText("błąd SQL - nie zmieniono ceny");
                System.out.println(ex);
            }
        }

        };


        //delegat osblugujacy zdarzenie pobrania ksiazek z bazy do Jcombobox
        private void PobierzListeKsiazekDoJcomboBox() throws SQLException {
            try (Connection conn=DriverManager.getConnection(jdbcconnect.jdbcUrl, jdbcconnect.jdbcUser, jdbcconnect.jdbcPass)) {
                String sql = "SELECT * FROM ksiazki";
                Statement prstm = conn.prepareStatement(sql);
                ResultSet res = prstm.executeQuery(sql);
                while(res.next()) {
                    String tytul = res.getString("tytul");
                    lista_ksiazki.addItem(tytul);

                }}
            catch (Exception ex){
                JOptionPane.showMessageDialog(null, ex);
            }

        }
    //delegat osblugujacy zdarzenie pobrania ksiazek z bazy do Jcombobox
    private void PobierzListeKlientowDoJcomboBox() throws SQLException {
        try (Connection conn=DriverManager.getConnection(jdbcconnect.jdbcUrl, jdbcconnect.jdbcUser, jdbcconnect.jdbcPass)) {
            String sql = "SELECT * FROM klienci";
            Statement prstm = conn.prepareStatement(sql);
            ResultSet res = prstm.executeQuery(sql);
            while(res.next()) {
                String pesel = res.getString("pesel");
                lista_klienci.addItem(pesel);

            }}
        catch (Exception ex){
            JOptionPane.showMessageDialog(null, ex);
        }

    }
    private void AktualnaListaZamowien(JList<String> lis) {
        try (Connection conn=DriverManager.getConnection(jdbcconnect.jdbcUrl, jdbcconnect.jdbcUser, jdbcconnect.jdbcPass)) {
            Statement stmt = conn.createStatement();
            String sql = "SELECT * FROM zamowienia";
            ResultSet res = stmt.executeQuery(sql);
            lmodel_zam.clear();
            while(res.next()) {
                String s = res.getString(1) + ": " + res.getString(2) + " " + res.getString(3) + ", " + res.getString(4) + " " + res.getString(5);
                lmodel_zam.addElement(s);

            }
        }
        catch (SQLException ex) {
            komunikat.setText("nie udało się zaktualizować listy klientów");
        }
    }

    //delegat do przycisku zapisz w paneli zamówienia
    private final ActionListener akc_zap_zam = new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                addNewOrderToDB();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }

        public void addNewOrderToDB() throws SQLException {
                try (Connection conn = DriverManager.getConnection(jdbcconnect.jdbcUrl, jdbcconnect.jdbcUser, jdbcconnect.jdbcPass)) {
                    String sql = "INSERT INTO zamowienia(pesel,ksiazka,kiedy,status) VALUES (?,?,?,?)";

                try {
                    PreparedStatement p_1 = conn.prepareStatement(sql);
                    String pesel = (String) lista_klienci.getSelectedItem();
                    String status = (String) lista_status.getSelectedItem();
                    String ksiazka = (String) lista_ksiazki.getSelectedItem();
                    String kiedy = pole_data_zam.getText();
                    p_1.setString(1, pesel);
                    p_1.setString(2, ksiazka);
                    p_1.setString(3, kiedy);
                    p_1.setString(4, status);
                    p_1.executeUpdate();
                    AktualnaListaZamowien(l_zam);
                    komunikat.setText("OK - dodano nowe zamówienie");
                } catch (SQLException e) {
                    e.printStackTrace();
                }

            }}
        };



    public Okno() throws SQLException {
        super("Księgarnia wysyłkowa");
        setSize(660, 460);
        setLocation(100, 100);
        setResizable(false);
        // panel do zarządzania klientami
        p_kli.setLayout(null);
        p_ksi.setLayout(null);
        p_zam.setLayout(null);
        // pole z peselem
        JLabel lab1 = new JLabel("pesel:");
        p_kli.add(lab1);
        lab1.setSize(100, 20);
        lab1.setLocation(40, 40);
        lab1.setHorizontalTextPosition(JLabel.RIGHT);
        p_kli.add(pole_pesel);
        pole_pesel.setSize(200, 20);
        pole_pesel.setLocation(160, 40);
        // pole z imieniem
        JLabel lab2 = new JLabel("imię:");
        p_kli.add(lab2);
        lab2.setSize(100, 20);
        lab2.setLocation(40, 80);
        lab2.setHorizontalTextPosition(JLabel.RIGHT);
        p_kli.add(pole_im);
        pole_im.setSize(200, 20);
        pole_im.setLocation(160, 80);
        // pole z nazwiskiem
        JLabel lab3 = new JLabel("nazwisko:");
        p_kli.add(lab3);
        lab3.setSize(100, 20);
        lab3.setLocation(40, 120);
        lab3.setHorizontalTextPosition(JLabel.RIGHT);
        p_kli.add(pole_naz);
        pole_naz.setSize(200, 20);
        pole_naz.setLocation(160, 120);
        // pole z datą urodzenia
        JLabel lab4 = new JLabel("data urodzenia:");
        p_kli.add(lab4);
        lab4.setSize(100, 20);
        lab4.setLocation(40, 160);
        lab4.setHorizontalTextPosition(JLabel.RIGHT);
        p_kli.add(pole_ur);
        pole_ur.setSize(200, 20);
        pole_ur.setLocation(160, 160);
        // pole z mailem
        JLabel lab5 = new JLabel("mail:");
        p_kli.add(lab5);
        lab5.setSize(100, 20);
        lab5.setLocation(40, 200);
        lab5.setHorizontalTextPosition(JLabel.RIGHT);
        p_kli.add(pole_mail);
        pole_mail.setSize(200, 20);
        pole_mail.setLocation(160, 200);
        // pole z adresem
        JLabel lab6 = new JLabel("adres:");
        p_kli.add(lab6);
        lab6.setSize(100, 20);
        lab6.setLocation(40, 240);
        lab6.setHorizontalTextPosition(JLabel.RIGHT);
        p_kli.add(pole_adr);
        pole_adr.setSize(200, 20);
        pole_adr.setLocation(160, 240);
        // pole z telefonem
        JLabel lab7 = new JLabel("telefon:");
        p_kli.add(lab7);
        lab7.setSize(100, 20);
        lab7.setLocation(40, 280);
        lab7.setHorizontalTextPosition(JLabel.RIGHT);
        p_kli.add(pole_tel);
        pole_tel.setSize(200, 20);
        pole_tel.setLocation(160, 280);
        // przycisk do zapisu klienta
        p_kli.add(przyc_zapisz_kli);
        przyc_zapisz_kli.setSize(200, 20);
        przyc_zapisz_kli.setLocation(160, 320);
        przyc_zapisz_kli.addActionListener(akc_zap_kli);
        // przycisk do usunięcia klienta
        p_kli.add(przyc_usun_kli);
        przyc_usun_kli.setSize(200, 20);
        przyc_usun_kli.setLocation(400, 320);
        przyc_usun_kli.addActionListener(akc_usun_kli);
        // lista z klientami
        p_kli.add(sp_kli);
        sp_kli.setSize(200, 260);
        sp_kli.setLocation(400, 40);
        l_kli.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        AktualnaListaKlientów(l_kli);

        // pole z isbn
        JLabel lab8 = new JLabel("Isbn:");
        p_ksi.add(lab8);
        lab8.setSize(100, 20);
        lab8.setLocation(40, 40);
        lab8.setHorizontalTextPosition(JLabel.RIGHT);
        p_ksi.add(pole_isbn);
        pole_isbn.setSize(200, 20);
        pole_isbn.setLocation(160, 40);

        // pole z autorem
        JLabel lab9 = new JLabel("Autor:");
        p_ksi.add(lab9);
        lab9.setSize(100, 20);
        lab9.setLocation(40, 80);
        lab9.setHorizontalTextPosition(JLabel.RIGHT);
        p_ksi.add(pole_autor);
        pole_autor.setSize(200, 20);
        pole_autor.setLocation(160, 80);

        // pole z tytułem
        JLabel lab10 = new JLabel("Tytuł:");
        p_ksi.add(lab10);
        lab10.setSize(100, 20);
        lab10.setLocation(40, 120);
        lab10.setHorizontalTextPosition(JLabel.RIGHT);
        p_ksi.add(pole_tytul);
        pole_tytul.setSize(200, 20);
        pole_tytul.setLocation(160, 120);

        // pole z typem
        JLabel lab11 = new JLabel("Typ:");
        p_ksi.add(lab11);
        lab11.setSize(100, 20);
        lab11.setLocation(40, 160);
        lab11.setHorizontalTextPosition(JLabel.RIGHT);
        p_ksi.add(lista_typ);
        lista_typ.setSize(200, 20);
        lista_typ.setLocation(160, 160);

        // pole z wydawnictwem
        JLabel lab12 = new JLabel("Wydawnictwo:");
        p_ksi.add(lab12);
        lab12.setSize(100, 20);
        lab12.setLocation(40, 200);
        lab12.setHorizontalTextPosition(JLabel.RIGHT);
        p_ksi.add(pole_wydawnictwo);
        pole_wydawnictwo.setSize(200, 20);
        pole_wydawnictwo.setLocation(160, 200);

        // pole z rokiem
        JLabel lab13 = new JLabel("Rok:");
        p_ksi.add(lab13);
        lab13.setSize(100, 20);
        lab13.setLocation(40, 240);
        lab13.setHorizontalTextPosition(JLabel.RIGHT);
        p_ksi.add(pole_rok);
        pole_rok.setSize(200, 20);
        pole_rok.setLocation(160, 240);

        // pole z cena
        JLabel lab14 = new JLabel("Cena:");
        p_ksi.add(lab14);
        lab14.setSize(100, 20);
        lab14.setLocation(40, 280);
        lab14.setHorizontalTextPosition(JLabel.RIGHT);
        p_ksi.add(pole_cena);
        pole_cena.setSize(200, 20);
        pole_cena.setLocation(160, 280);

        // przycisk do zapisu ksiazki
        p_ksi.add(przyc_zapisz_ksi);
        przyc_zapisz_ksi.setSize(200, 20);
        przyc_zapisz_ksi.setLocation(160, 320);
        przyc_zapisz_ksi.addActionListener(akc_zap_ksi);

        // przycisk do usunięcia ksiazki
        p_ksi.add(przyc_usun_ksi);
        przyc_usun_ksi.setSize(200, 20);
        przyc_usun_ksi.setLocation(400, 320);
        przyc_usun_ksi.addActionListener(akc_usun_ksi);

        // przycisk do zmiany ceny ksiazki
        p_ksi.add(przyc_zmien_cene);
        przyc_zmien_cene.setSize(200, 20);
        przyc_zmien_cene.setLocation(280, 350);
        przyc_zmien_cene.addActionListener(akc_zmien_cene);

        //lista z ksiazkami
        p_ksi.add(sp_ksi);
        sp_ksi.setSize(200, 260);
        sp_ksi.setLocation(400, 40);
        l_ksi.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        AktualnaListaKsiazek(l_ksi);


        //panel zamówienia

        //pole ksiazki

        JLabel lab15 = new JLabel("Ksiązki:");
        p_zam.add(lab15);
        lab15.setSize(100, 20);
        lab15.setLocation(40, 160);
        lab15.setHorizontalTextPosition(JLabel.RIGHT);
        p_zam.add(lista_ksiazki);
        lista_ksiazki.setSize(200, 20);
        lista_ksiazki.setLocation(160, 160);
        PobierzListeKsiazekDoJcomboBox();

        //pole klienci

        JLabel lab16 = new JLabel("Klienci:");
        p_zam.add(lab16);
        lab16.setSize(100, 20);
        lab16.setLocation(40, 200);
        lab16.setHorizontalTextPosition(JLabel.RIGHT);
        p_zam.add(lista_klienci);
        lista_klienci.setSize(200, 20);
        lista_klienci.setLocation(160, 200);
        PobierzListeKlientowDoJcomboBox();

        //pole data
        JLabel lab17 = new JLabel("Kiedy:");
        p_zam.add(lab17);
        lab17.setSize(100, 20);
        lab17.setLocation(40, 240);
        lab17.setHorizontalTextPosition(JLabel.RIGHT);
        p_zam.add(pole_data_zam);
        pole_data_zam.setSize(200, 20);
        pole_data_zam.setLocation(160, 240);

        //pole status
        JLabel lab18 = new JLabel("Status:");
        p_zam.add(lab18);
        lab18.setSize(100, 20);
        lab18.setLocation(40, 280);
        lab18.setHorizontalTextPosition(JLabel.RIGHT);
        p_zam.add(lista_status);
        lista_status.setSize(200, 20);
        lista_status.setLocation(160, 280);


        //przycisk zapisz zamowienie
        p_zam.add(przyc_zapisz_zam);
        przyc_zapisz_zam.setSize(200, 20);
        przyc_zapisz_zam.setLocation(160, 320);
        przyc_zapisz_zam.addActionListener(akc_zap_zam);

        //lista z zamowieniami
        p_zam.add(sp_zam);
        sp_zam.setSize(200, 260);
        sp_zam.setLocation(400, 40);
        l_zam.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        AktualnaListaZamowien(l_zam);




        // panel z zakładkami
        tp.addTab("klienci", p_kli);
        tp.addTab("książki", p_ksi);
        tp.addTab("zamówienia", p_zam);
        getContentPane().add(tp, BorderLayout.CENTER);
        // pole na komentarze
        komunikat.setEditable(false);
        getContentPane().add(komunikat, BorderLayout.SOUTH);
        // pokazanie okna
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
    }
}

public class Ksiegarnia {
    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        new Okno();
    }}

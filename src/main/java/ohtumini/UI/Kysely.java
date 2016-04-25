package ohtumini.UI;

import java.io.IOException;
import java.util.Locale;
import ohtumini.bibtex.BibTexTiedosto;
import ohtumini.io.IO;
import viitteet.Article;
import viitteet.Book;
import viitteet.Viite;
import viitteet.Inproceedings;
import viitteet.Viitelista;


public class Kysely {

    private IO io;
    private Viitelista viitteet;
    private Tulosteet tuloste;
    private boolean running;
    private String komento;

    public Kysely(IO io) {
        this.io = io;
        this.viitteet = new Viitelista("default");
        this.tuloste = new Tulosteet(io);
        this.running = true;

    }

    public void run() {
        String komentoNoCapitalizationChanges;
        tuloste.tulostaKomennot();
        while (running) {
            komentoNoCapitalizationChanges = io.readLine(">");
            this.komento = komentoNoCapitalizationChanges.toLowerCase(Locale.ROOT);
            aloitaKysely(komentoNoCapitalizationChanges);
        }
    }

    public void aloitaKysely(String komentoNoCapitalizationChanges) {
        luoViite();
        tulostaViite();
        asetaKentta(komentoNoCapitalizationChanges);
        tulostaBibTeX();
        luoBibTex();
        komennot();
        tallennaTiedostoon();
        listaaViitteet();
        poistaViite();
        lataaTiedosto();
        lopeta();
        lopetaTallentamatta();

    }

    //luo-viite 
    //"1"
    public void luoViite() {
        if (komento.startsWith("luo-viite") || komento.split(" ")[0].compareTo("1") == 0) {
            tuloste.tulostaLuoUusiViiteKomennot();
            io.print("Anna viitteen tyyppi");
            String kasky = io.readLine("> ");
            aloitaAlikysely(kasky);
        }
    }

    public void aloitaAlikysely(String kasky) {
        Viite uusiViite;
        if (kasky.startsWith("article") || kasky.startsWith("1")) {
            io.print("Uusi article viite luotu");
            uusiViite = new Article();
        } else if (kasky.startsWith("book") || kasky.startsWith("2")) {
            io.print("Uusi book viite luotu");
            uusiViite = new Book();
        } else if (kasky.startsWith("inproceedings") || kasky.startsWith("3")) {
            io.print("Uusi inproceedings viite luotu");
            uusiViite = new Inproceedings();
        } else {
            io.print("\n");
            io.print("Viitettä ei luotu.");
            return;
        }
        String syote;
        for (String kentta : uusiViite.kentat()) {
            io.print("Anna kentta " + kentta + (uusiViite.onkoPakollinen(kentta) ? "*" : "") + ":");
            do {
                syote = io.readLine("> ");
            } while (uusiViite.onkoPakollinen(kentta) && syote.length() == 0);
            if (syote.length() > 0) {
                uusiViite.lisaaTieto(kentta, syote);
            }
        }
        io.print("Anna viitteelle tunniste:");
        do {
            syote = io.readLine("> ");
        } while (viitteet.get(syote) != null || syote.length() == 0); //Ei anneta luoda
        uusiViite.setTunniste(syote);                                 //useaa viitettä joilla
        viitteet.add(uusiViite);                                      //sama tunniste
        io.print("Viite luotu");
        viiteListanKoko();
    }

    public void viiteListanKoko() {
        io.print("Viitteitä yhteensä: " + viitteet.size());
    }

    //tulosta-viite <viitteen numero> 
    //"2"
    public void tulostaViite() {
        String tunniste;
        if (komento.startsWith("tulosta-viite") || komento.startsWith("2")) {
            if (komento.split(" ").length < 2) {
                tunniste = io.readLine("Anna tunniste\n> ");
            } else {
                tunniste = komento.split(" ")[1];
            }

            if (viitteet.get(tunniste) == null) {
                io.print("Tunnistetta ei löytynyt");
            } else {
                io.print(viitteet.get(tunniste).toString());
            }
        }
    }

    // aseta-kentta <viitteen numero> <kentan nimi> <arvo> 
    //"3"
    public void asetaKentta(String komentoNoCapitalizationChanges) {
        if (komento.startsWith("aseta-kentta") || komento.startsWith("3")) {
            String tunniste;
            String kentta;
            String avain;
            tunniste = tunnisteenTarkastusJaKysely();
            if (tunnistettaEiLoydy(tunniste)) return;
            kentta = kentanTarkastusJaKysely();
            io.print("Tällä hetkellä kentän " + kentta + " arvo on \"" + viitteet.get(tunniste).lueTieto(kentta) + "\"");
            avain = avaimenTarkastusJaKysely(komentoNoCapitalizationChanges);
            viitteet.get(tunniste).lisaaTieto(kentta, avain);
            io.print("Asettaminen onnistui");
            io.print(tunniste + ":" + kentta + " = \"" + viitteet.get(tunniste).lueTieto(kentta) + "\"");
        }
    }
    
    //tarkastaa onko tunniste asetettu aseta-kentta syötteen yhteydessä. Jos ei niin, pyytää asettamaan tunnisteen.
    public String tunnisteenTarkastusJaKysely() {
        String tunniste;
        if (komento.split(" ").length > 1) {
            tunniste = komento.split(" ")[1];
        } else {
            tunniste = io.readLine("Anna tunniste\n> ");
        }
        return tunniste;
    }

    //Tarkastaa onko annettu tunniste validi.
    public boolean tunnistettaEiLoydy(String tunniste) {
        if (viitteet.get(tunniste) == null) {
            io.print("Tunnistetta ei löytynyt");
            return true;
        }
        return false;
    }
    
    //tarkastaa onko kentta asetettu aseta-kentta syötteen yhteydessä. Jos ei niin, pyytää asettamaan kentän.
    public String kentanTarkastusJaKysely() {
        String kentta;
        if (komento.split(" ").length > 2) {
            kentta = komento.split(" ")[2];
        } else {
            io.print("Anna kentta: ");
            kentta = io.readLine("> ");
        }
        return kentta;
    }

    //tarkastaa onko avain asetettu aseta-kentta syötteen yhteydessä. Jos ei niin, pyytää asettamaan avaimen.
    public String avaimenTarkastusJaKysely(String komentoNoCapitalizationChanges) {
        String avain;
        if (komento.split(" ").length > 3) {
            avain = komentoNoCapitalizationChanges.split(" ", 4)[3];
        } else {
            io.print("Anna avain: ");
            avain = io.readLine("> ");
        }
        return avain;
    }

    // tulosta-bibtex <viitteen numero> 
    //"4"
    public void tulostaBibTeX() {
        if (komento.startsWith("tulosta-bibtex") || komento.startsWith("4")) {
            String tunniste;
            if (komento.split(" ").length > 1) {
                tunniste = komento.split(" ")[1];
            } else {
                tunniste = io.readLine("Anna tunniste\n> ");
            }
            if (viitteet.get(tunniste) == null) {
                io.print("Tunnistetta ei löytynyt");
                return;
            }
            io.print(viitteet.get(tunniste).luoBibTeX());
        }
    }

    // luo bibtext tiedoston viitteistä 
    //"5"
    public void luoBibTex() {
        if (komento.startsWith("luo-bibtex-tiedosto") || komento.startsWith("5")) {
            io.print("Anna tiedostonimi mihin BibTex tulostetaan:");
            try {
                String nimi = io.readLine("> ");
                BibTexTiedosto t = new BibTexTiedosto(nimi);
                for (Viite v : viitteet) {
                    t.lisaaViiteTiedostoon(v.luoBibTeX());
                }
                io.print("Viitteet löytyvät tiedostosta " + nimi + ".bib");
            } catch (IOException ex) {
                io.print("Tiedoston luominen ei onnistu.");
                io.print("Tarkista kansion kirjoitusoikeudet.");
            }
        }
    }
    
    //listaa viitteet
    //"6"
    public void listaaViitteet() {
        if (komento.startsWith("listaa-viitteet") || komento.startsWith("6")) {
            String pitkaVaiLyhyt;
            io.print("Tulostetaanko vain lyhyt versio kustakin viitteestä? Y/n");
            pitkaVaiLyhyt = io.readLine("> ");
            io.print(viitteet.size() + " viitettä");
            if (pitkaVaiLyhyt.compareToIgnoreCase("n") == 0) {
                for (Viite v : viitteet) {
                    io.print(v.getTunniste() + " (" + v.annaViitteenTyypinNimi() + ")");
                    io.print(v.toString());
                }
            }
            else {
                for (Viite v : viitteet) {
                    io.print(v.getTunniste() + " (" + v.annaViitteenTyypinNimi() + ")");
                }
            }
        }
    }
    
    public void poistaViite() {
        String tunniste;
        if (komento.startsWith("poista-viite") || komento.startsWith("7")) {
            if (komento.split(" ").length < 2) {
                tunniste = io.readLine("Anna tunniste\n> ");
            } else {
                tunniste = komento.split(" ")[1];
            }

            if (viitteet.get(tunniste) == null) {
                io.print("Tunnistetta ei löytynyt");
            } else {
                io.print("Poistetaan viite " + viitteet.get(tunniste).getTunniste());
                io.print(viitteet.get(tunniste).toString());
                if (io.readLine("Haluatko varmasti poistaa tämän tunnisteen? y/N\n> ").compareToIgnoreCase("y") == 0) {
                    viitteet.remove(tunniste);
                }
            }
        }
    }

    //tulostaa komennot 
    //"7"
    public void komennot() {
        if (komento.startsWith("komennot") || komento.startsWith("8") || komento.length() == 0) {
            tuloste.tulostaKomennot();
        }
    }

    public void lataaTiedosto() {
        if (komento.startsWith("lataa") || komento.startsWith("9")) {
            if (viitteet.size() > 0) {
                if (!varmistus("Sinulla on viitteitä, muutokset katoavat jos lataat päälle uudet! Oletko varma?")) {
                    return;
                }
            }
            TallennuksenLatausKysely k = new TallennuksenLatausKysely(io);
            k.suorita();
            if (k.getLadattuViitelista() != null) {
                viitteet = k.getLadattuViitelista();
            } else {
                io.print("Viitteitten lataaminen ei onnistunut");
            }
        }
    }

    public void tallennaTiedostoon() {
        if (komento.startsWith("tallenna") || komento.startsWith("10")) {
            new TallennusKysely(io, viitteet).suorita();
        }
    }

    // luo bibtext tiedoston viitteistä 
    //"10"
    public void lopeta() {
        if (komento.startsWith("lopeta") || komento.startsWith("11")) {
            if (varmistus("Tallennetaanko muutokset?")) {
                new TallennusKysely(io, viitteet).suorita();
            }
            running = false;
        }
    }

    //EasyB:tä varten ainakin Joda Koska startsWith
    public void lopetaTallentamatta() {
        if (komento.startsWith("tallentamatta-lopeta")) {
            running = false;
        }
    }

    //hyrr hirviö, koska aika
    private boolean varmistus(String kysymys) {
        String yn = "";
        while (!(yn.startsWith("n") || yn.startsWith("e") || yn.startsWith("y") || yn.startsWith("k"))) {
            io.print(kysymys);
            yn = io.readLine("y/n > ").toLowerCase();
        }
        return (yn.startsWith("y") || yn.startsWith("k"));
    }
}

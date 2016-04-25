
package ohtumini.UI;

import ohtumini.io.IO;

/**
 *
 * @author samukaup
 */
public class Tulosteet {
    private IO io;

    public Tulosteet(IO io) {
        this.io = io;
    }
    
    public void tulostaKomennot() {
        io.print("\n" + "Käytettävissä olevat komennot:");
        io.print("- 1 / luo-viite:              Luo uuden viitteen");
        io.print("- 2 / tulosta-viite<>:        Tulostaa viitteen annetun <hakuavaimen> perusteella");
        io.print("- 3 / aseta-kentta<><><>:     Asettaa viitteelle <viitteen numeron> <kentän nimen> <arvon>");
        io.print("- 4 / tulosta-bibtex<>:       Tulostaa BibTex tiedoston annetun <hakuavaimen> perusteella");
        io.print("- 5 / luo-bibtex-tiedosto:    Luo BibText tiedoston");
        io.print("- 6 / tulosta-viitteet:       Tulostaa kaikki viitteet");
        io.print("- 7 / poista-viite:               Poistaa halutun viitteen");
        io.print("- 8 / komennot:               Tulostaa käytettävissä olevat komennot");
        io.print("- 9 / lataa:                  Lataa viitteet tiedostosta");
        io.print("- 10 / tallenna:               Tallentaa viitteet tiedostoon");
        io.print("- 11 / lopeta:                 Lopettaa ohjelman suorituksen \n");
    }

    public void tulostaLuoUusiViiteKomennot() {
        io.print("\n" + "Käytettävissä olevat komennot:");
        io.print("- 1 / article:                Luo uuden article viitteen");
        io.print("- 2 / book:                   Luo uuden book viitteen");
        io.print("- 3 / inproceedings:          Luo uuden inproceedings viitteen");
        io.print("- 4 / palaa:                  Siirtää takaisin päävalikkoon \n");
    }
}

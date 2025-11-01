package ma.projet.test;

import ma.projet.beans.Femme;
import ma.projet.beans.Homme;
import ma.projet.beans.Mariage;
import ma.projet.services.FemmeService;
import ma.projet.services.HommeService;
import ma.projet.services.MariageService;
import ma.projet.util.HibernateUtil;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

public class TestEtatCivil {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext ctx =
                new AnnotationConfigApplicationContext(HibernateUtil.class);

        HommeService hommeService = (HommeService) ctx.getBean("hommeService");
        FemmeService femmeService = (FemmeService) ctx.getBean("femmeService");
        MariageService mariageService = (MariageService) ctx.getBean("mariageService");

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

            System.out.println("=== Création des données ===\n");

            // Créer 10 femmes
            Femme f1 = new Femme("EL MANSOURI", "SARA", "0611111111", "Casablanca", sdf.parse("05/02/1978"));
            Femme f2 = new Femme("ZAHRAOUI", "RANIA", "0612222222", "Rabat", sdf.parse("11/07/1980"));
            Femme f3 = new Femme("CHAFIQ", "HIND", "0613333333", "Fès", sdf.parse("09/09/1985"));
            Femme f4 = new Femme("TAHAR", "IMANE", "0614444444", "Marrakech", sdf.parse("21/03/1975"));
            Femme f5 = new Femme("BENALI", "NAWAL", "0615555555", "Agadir", sdf.parse("17/01/1982"));
            Femme f6 = new Femme("AIT OUARZAZATE", "LATIFA", "0616666666", "Ouarzazate", sdf.parse("30/11/1979"));
            Femme f7 = new Femme("EL KHALFI", "SAMIRA", "0617777777", "Tanger", sdf.parse("02/05/1983"));
            Femme f8 = new Femme("EL HASSANI", "FATIMA", "0618888888", "Tétouan", sdf.parse("13/06/1981"));
            Femme f9 = new Femme("RAHIMI", "NOURA", "0619999999", "Oujda", sdf.parse("23/09/1976"));
            Femme f10 = new Femme("AMIRI", "HOUDA", "0610000000", "Meknès", sdf.parse("04/12/1973"));

            femmeService.create(f1);
            femmeService.create(f2);
            femmeService.create(f3);
            femmeService.create(f4);
            femmeService.create(f5);
            femmeService.create(f6);
            femmeService.create(f7);
            femmeService.create(f8);
            femmeService.create(f9);
            femmeService.create(f10);

            // Créer 5 hommes
            Homme h1 = new Homme("BENJELLOUN", "OMAR", "0621111111", "Casablanca", sdf.parse("10/03/1970"));
            Homme h2 = new Homme("EL FASSI", "YASSINE", "0622222222", "Rabat", sdf.parse("12/08/1972"));
            Homme h3 = new Homme("TAZI", "ANAS", "0623333333", "Fès", sdf.parse("14/09/1968"));
            Homme h4 = new Homme("LAKHDAR", "MOUNIR", "0624444444", "Marrakech", sdf.parse("22/10/1975"));
            Homme h5 = new Homme("HAMDAOUI", "SAID", "0625555555", "Agadir", sdf.parse("07/06/1978"));

            hommeService.create(h1);
            hommeService.create(h2);
            hommeService.create(h3);
            hommeService.create(h4);
            hommeService.create(h5);

            // Créer des mariages
            // Mariages de h1 (BENJELLOUN OMAR)
            Mariage m1 = new Mariage(sdf.parse("10/02/1993"), sdf.parse("15/08/1996"), 1, h1, f4);
            Mariage m2 = new Mariage(sdf.parse("20/09/1996"), h1, f1);
            m2.setNbrEnfant(3);
            Mariage m3 = new Mariage(sdf.parse("05/04/2002"), h1, f2);
            m3.setNbrEnfant(2);
            Mariage m4 = new Mariage(sdf.parse("22/06/2008"), h1, f3);
            m4.setNbrEnfant(1);

            mariageService.create(m1);
            mariageService.create(m2);
            mariageService.create(m3);
            mariageService.create(m4);

            // Mariages de h2
            Mariage m5 = new Mariage(sdf.parse("15/03/1995"), h2, f5);
            m5.setNbrEnfant(2);
            Mariage m6 = new Mariage(sdf.parse("12/09/2000"), h2, f6);
            m6.setNbrEnfant(1);
            mariageService.create(m5);
            mariageService.create(m6);

            // Mariages de h3
            Mariage m7 = new Mariage(sdf.parse("10/11/1990"), sdf.parse("20/07/1994"), 0, h3, f7);
            Mariage m8 = new Mariage(sdf.parse("25/12/1996"), h3, f8);
            m8.setNbrEnfant(3);
            mariageService.create(m7);
            mariageService.create(m8);

            // Mariage multiple pour f7 (remariée)
            Mariage m9 = new Mariage(sdf.parse("10/05/2001"), h4, f7);
            m9.setNbrEnfant(2);
            mariageService.create(m9);

            // Mariage de h5
            Mariage m10 = new Mariage(sdf.parse("14/07/2003"), h5, f9);
            m10.setNbrEnfant(2);
            mariageService.create(m10);

            System.out.println("Données créées avec succès!\n");
            System.out.println("=".repeat(80));

            // TESTS
            System.out.println("\n1. Liste des femmes:");
            femmeService.afficherFemmes();

            System.out.println("\n2. Femme la plus âgée:");
            Femme oldest = femmeService.findOldestFemme();
            if (oldest != null) {
                System.out.println("La femme la plus âgée est: " + oldest.getPrenom() + " " +
                        oldest.getNom() + " (Née le: " + sdf.format(oldest.getDateNaissance()) + ")");
            }

            System.out.println("\n3. Épouses d'un homme entre deux dates:");
            hommeService.afficherEpousesEntreDates(h1.getId(),
                    sdf.parse("01/01/1995"),
                    sdf.parse("31/12/2005"));

            System.out.println("\n4. Nombre d'enfants d'une femme entre deux dates:");
            int nbrEnfants = femmeService.countEnfantsBetweenDates(f1.getId(),
                    sdf.parse("01/01/1995"),
                    sdf.parse("31/12/2005"));
            System.out.println("Nombre d'enfants de " + f1.getPrenom() + " " + f1.getNom() +
                    " entre 01/01/1995 et 31/12/2005: " + nbrEnfants);

            System.out.println("\n5. Femmes mariées deux fois ou plus:");
            List<Femme> femmesMultiMariees = femmeService.findFemmesMarriedTwiceOrMore();
            if (femmesMultiMariees.isEmpty()) {
                System.out.println("Aucune femme mariée deux fois ou plus.");
            } else {
                for (Femme f : femmesMultiMariees) {
                    System.out.println("  - " + f.getPrenom() + " " + f.getNom() +
                            " (Nombre de mariages: " + f.getMariages().size() + ")");
                }
            }

            System.out.println("\n6. Hommes mariés à quatre femmes entre deux dates:");
            hommeService.afficherHommesMarieQuatreFemmes(
                    sdf.parse("01/01/1993"),
                    sdf.parse("31/12/2010"));

            System.out.println("\n7. Mariages d'un homme avec détails:");
            hommeService.afficherMariagesHomme(h1.getId());

            System.out.println("\n" + "=".repeat(80));

        } catch (ParseException e) {
            e.printStackTrace();
        } finally {
            ctx.close();
        }
    }
}

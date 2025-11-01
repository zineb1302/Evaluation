package ma.projet.services;

import ma.projet.beans.Homme;
import ma.projet.beans.Mariage;
import ma.projet.dao.IDao;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Join;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
@Transactional
public class HommeService implements IDao<Homme> {

    private final SessionFactory sessionFactory;

    @Autowired
    public HommeService(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    protected Session getSession() {
        return sessionFactory.getCurrentSession();
    }

    @Override
    public void create(Homme o) {
        getSession().persist(o);
    }

    @Override
    public Homme getById(int id) {
        return getSession().get(Homme.class, id);
    }

    @Override
    public List<Homme> getAll() {
        return getSession().createQuery("from Homme", Homme.class).list();
    }

    @Override
    public void update(Homme o) {
        getSession().merge(o);
    }

    @Override
    public void delete(Homme o) {
        getSession().remove(o);
    }

    // Afficher les épouses d'un homme entre deux dates
    public void afficherEpousesEntreDates(int hommeId, Date dateDebut, Date dateFin) {
        String hql = "SELECT m.femme FROM Mariage m WHERE m.homme.id = :hommeId " +
                "AND m.dateDebut BETWEEN :dateDebut AND :dateFin";

        List<?> femmes = getSession().createQuery(hql)
                .setParameter("hommeId", hommeId)
                .setParameter("dateDebut", dateDebut)
                .setParameter("dateFin", dateFin)
                .list();

        Homme h = getById(hommeId);
        System.out.println("Épouses de " + h.getNom() + " " + h.getPrenom() +
                " entre " + dateDebut + " et " + dateFin + ":");
        femmes.forEach(f -> System.out.println("  - " + f));
    }

    // Afficher le nombre d'hommes mariés à 4 femmes entre deux dates (Criteria API)
    public void afficherHommesMarieQuatreFemmes(Date dateDebut, Date dateFin) {
        CriteriaBuilder cb = getSession().getCriteriaBuilder();
        CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);
        Root<Homme> hommeRoot = cq.from(Homme.class);
        Join<Object, Object> mariageJoin = hommeRoot.join("mariages");

        cq.multiselect(
                hommeRoot.get("id"),
                hommeRoot.get("nom"),
                hommeRoot.get("prenom"),
                cb.count(mariageJoin)
        );

        cq.where(
                cb.between(mariageJoin.get("dateDebut"), dateDebut, dateFin)
        );

        cq.groupBy(hommeRoot.get("id"), hommeRoot.get("nom"), hommeRoot.get("prenom"));
        cq.having(cb.equal(cb.count(mariageJoin), 4));

        List<Object[]> results = getSession().createQuery(cq).getResultList();

        System.out.println("Hommes mariés à 4 femmes entre " + dateDebut + " et " + dateFin + ":");
        for (Object[] result : results) {
            System.out.println("  - " + result[1] + " " + result[2] + " (ID: " + result[0] +
                    ", Nombre de mariages: " + result[3] + ")");
        }
    }

    // Afficher les mariages d'un homme avec tous les détails
    public void afficherMariagesHomme(int hommeId) {
        Homme h = getById(hommeId);
        if (h == null) {
            System.out.println("Homme introuvable!");
            return;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        System.out.println("Nom : " + h.getNom() + " " + h.getPrenom());

        // Mariages en cours
        String hqlEnCours = "FROM Mariage m WHERE m.homme.id = :hommeId AND m.dateFin IS NULL";
        List<Mariage> mariagesEnCours = getSession().createQuery(hqlEnCours, Mariage.class)
                .setParameter("hommeId", hommeId)
                .list();

        if (!mariagesEnCours.isEmpty()) {
            System.out.println("Mariages En Cours :");
            int i = 1;
            for (Mariage m : mariagesEnCours) {
                System.out.printf("%d. Femme : %s %s\tDate Début : %s\tNbr Enfants : %d%n",
                        i++,
                        m.getFemme().getPrenom(),
                        m.getFemme().getNom(),
                        sdf.format(m.getDateDebut()),
                        m.getNbrEnfant());
            }
        }

        // Mariages échoués
        String hqlEchoues = "FROM Mariage m WHERE m.homme.id = :hommeId AND m.dateFin IS NOT NULL";
        List<Mariage> mariagesEchoues = getSession().createQuery(hqlEchoues, Mariage.class)
                .setParameter("hommeId", hommeId)
                .list();

        if (!mariagesEchoues.isEmpty()) {
            System.out.println("\nMariages échoués :");
            int i = 1;
            for (Mariage m : mariagesEchoues) {
                System.out.printf("%d. Femme : %s %s\tDate Début : %s%n",
                        i++,
                        m.getFemme().getPrenom(),
                        m.getFemme().getNom(),
                        sdf.format(m.getDateDebut()));
                System.out.printf("   Date Fin : %s\tNbr Enfants : %d%n",
                        sdf.format(m.getDateFin()),
                        m.getNbrEnfant());
            }
        }
    }
}

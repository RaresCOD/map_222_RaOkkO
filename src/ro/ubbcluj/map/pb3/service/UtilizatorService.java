package ro.ubbcluj.map.pb3.service;

import jdk.jshell.execution.Util;
import ro.ubbcluj.map.pb3.Conexitate.DFS;
import ro.ubbcluj.map.pb3.domain.Prietenie;
import ro.ubbcluj.map.pb3.domain.Tuple;
import ro.ubbcluj.map.pb3.domain.Utilizator;
import ro.ubbcluj.map.pb3.domain.validators.ValidationException;
import ro.ubbcluj.map.pb3.repository.Repository;

import java.util.Iterator;
import java.util.List;

/**
 * service
 */
public class UtilizatorService {
    Repository<Long, Utilizator> repo;
    Repository<Tuple<Long, Long>, Prietenie> repoFriend;

    private void makeFriends() {
        for(Prietenie curent : repoFriend.findAll()) {
            Long left = curent.getId().getLeft();
            Long right = curent.getId().getRight();
            Utilizator util1 = repo.findOne(left);
            Utilizator util2 = repo.findOne(right);
            util1.addFriend(util2);
            util2.addFriend(util1);
        }
    }

    private Long lastID() {
        Long lID = 0L;
        for(Utilizator util : repo.findAll()) {
            lID = util.getId();
        }
        return lID;
    }

    /**
     *
     * @param id1 - id ul primului prieten
     * @param id2 - id ul celui de al doilea prieten
     * @return - prietenia daca exista, altfel null
     */
    public Prietenie FindOneFriend(Long id1, Long id2) {
        return repoFriend.findOne(new Tuple(id1, id2));
    }

    /**
     *
     * @param repo user repo
     * @param repoFriend friendship repo
     */
    public UtilizatorService(Repository<Long, Utilizator> repo, Repository<Tuple<Long, Long>, Prietenie> repoFriend) {
        this.repo = repo;
        this.repoFriend = repoFriend;
        makeFriends();
    }

    /**
     *
     * @param firstName fn
     * @param lastName ln
     * @return add user
     */
    public Utilizator addUtilizator(String firstName, String lastName) {
        Utilizator nou = new Utilizator(firstName, lastName);
        Long id = lastID() + 1L;
        nou.setId(id);
        try {
            Utilizator util = repo.save(nou);
            return util;
        } catch (IllegalArgumentException i) {
            System.out.println(i.getMessage());
        } catch (ValidationException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    /**
     *
     * @param id id
     * @return delete user
     */
    public Utilizator deleteUtilizator(Long id) {
        try {
            Utilizator utilizator = repo.findOne(id);
            if (utilizator == null) {
                System.out.println("Id inexistent");
                return null;
            }
            for(Utilizator util : repo.findAll()) {

                    util.deleteFriend(utilizator);
                    repoFriend.delete(new Tuple(util.getId(), utilizator.getId()));
                    repoFriend.delete(new Tuple(utilizator.getId(), util.getId()));

            }
            repo.delete(id);
            return utilizator;
        } catch (IllegalArgumentException i) {
            System.out.println(i.getMessage());
        }
        return null;
    }

    /**
     *
     * @param id id
     * @param firstName fn
     * @param lastName ln
     * @return update user
     */
    public Utilizator updateUtilizator(Long id, String firstName, String lastName) {
        Utilizator nou = new Utilizator(firstName, lastName);
        nou.setId(id);
        try {
            Utilizator util = repo.update(nou);
            if (util != null) {
                System.out.println("Utilizator inexistent");
            }
            return util;
        } catch (IllegalArgumentException i) {
            System.out.println(i.getMessage());
        } catch (ValidationException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    /**
     *
     * @param id1 user id
     * @param id2 user id
     */
    public void addFriend(Long id1, Long id2) {
        Prietenie prietenie = new Prietenie();
        Tuple<Long, Long> tuple = new Tuple(id1, id2);

        if (repo.findOne(id1) == null) {
            throw new ValidationException("User inexistent!");
        }
        if (repo.findOne(id2) == null) {
            throw new ValidationException("User inexistent!");
        }

        prietenie.setId(tuple);

        repoFriend.save(prietenie);
        Utilizator util1 = repo.findOne(id1);
        Utilizator util2 = repo.findOne(id2);
        util1.addFriend(util2);
        util2.addFriend(util1);
    }

    /**
     *
     * @param id1 user id
     * @param id2 user id
     */
    public void deleteFriend(Long id1, Long id2) {
        if (repo.findOne(id1) == null) {
            throw new ValidationException("User inexistent!");
        }
        if (repo.findOne(id2) == null) {
            throw new ValidationException("User inexistent!");
        }
        if (repoFriend.findOne(new Tuple(id1, id2)) == null) {
            throw new ValidationException("Prietenie inexistenta!");
        }
        Utilizator util1 = repo.findOne(id1);
        Utilizator util2 = repo.findOne(id2);
        util1.deleteFriend(util2);
        util2.deleteFriend(util1);
        repoFriend.delete(new Tuple(id1, id2));
    }

    /**
     *
     * @param id id
     * @return specific frineds
     */
    public List<Utilizator> getFriends(Long id)  {
        Utilizator util = repo.findOne(id);
        return util.getFriends();
    }

    /**
     *
     * @return all users
     */
    public Iterable<Utilizator> getAll() {
        return repo.findAll();
    }

    /**
     *
     * @return all friends
     */
    public Iterable<Prietenie> getAllFriends() {
        return repoFriend.findAll();
    }

    /**
     *
     * @return nb of communites
     */
    public int numarComunitati() {
        DFS dfs = new DFS(Math.toIntExact(lastID()), repoFriend.findAll(), repo.findAll());
        return dfs.execute1();
    }

    /**
     * largest community
     * @return int
     */
    public int JonnyVorbaretu() {
        DFS dfs = new DFS(Math.toIntExact(lastID()), repoFriend.findAll(), repo.findAll());
        return dfs.execute2();
    }


}

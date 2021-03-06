package model.entity;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.annotations.NaturalId;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Map;

@Entity
public class Section implements Serializable {

    @Getter @Setter
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Integer id;

    @Getter @Setter
    @Column(length = 255)
    protected String description;


    @Getter @Setter
    @Column(length = 50, nullable = false, unique = true)
    @NaturalId(mutable = true)
    protected String name;

    @Getter @Setter
    @Column(length = 4096)
    protected String picture;

    @Getter @Setter
    @Column(length = 4096)
    protected String banner;

    @OneToMany(mappedBy="section")
    @MapKeyJoinColumn(name="user_id", updatable = false, insertable = false)
    @LazyCollection(LazyCollectionOption.EXTRA)
    protected Map<User, Follow> follows;

    /**
     * Ottieni un'istanza di <pre>Follow</pre> tra la sezione in questione e l'utente specificato (o <pre>null</pre> se tale istanza non è presente)
     * @param user Riferimento all'utente
     * @return Un oggetto {@link Follow}
     */
    public Follow getFollow(User user){
        return follows.get(user);
    }

    /**
     * Ottieni il numero di seguaci
     * @return Il numero di seguaci alla sezione in questione
     */
    public int getFollowCount(){
        return follows.size();
    }

    public Section(){}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Section)) return false;
        Section section = (Section) o;
        return id.equals(section.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}

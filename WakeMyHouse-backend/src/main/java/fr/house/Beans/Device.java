package fr.house.Beans;

import lombok.*;
import jakarta.persistence.*;

@Entity
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class Device {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String hostname;
    private String mac;
    private String ip;
    private Boolean status;


}

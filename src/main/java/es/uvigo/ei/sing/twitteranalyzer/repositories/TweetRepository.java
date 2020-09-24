package es.uvigo.ei.sing.twitteranalyzer.repositories;

import es.uvigo.ei.sing.twitteranalyzer.entities.TweetEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface TweetRepository extends CrudRepository<TweetEntity, Long> {

    Set<TweetEntity> findAll();

    @Query("SELECT t FROM TweetEntity t WHERE (LOWER(t.text) like '% port %'" +
            "OR LOWER(t.text) like '%#port %'" +
            "OR LOWER(t.text) like '%porto%')" +
            "AND t.lang like 'en'")
    Set<TweetEntity> findAllEnglishPorto();

    @Query("SELECT t FROM TweetEntity t WHERE (LOWER(t.text) like '%douro%')" +
            "AND t.lang like 'en'")
    Set<TweetEntity> findAllEnglishDouro();

    @Query("SELECT t FROM TweetEntity t WHERE (" +
            "LOWER(t.text) LIKE '% beiras %'" +
            "OR LOWER(t.text) LIKE '% dao %'" +
            "OR LOWER(t.text) LIKE '% dão %'" +
            "OR LOWER(t.text) LIKE '% moura %'" +
            "OR LOWER(t.text) LIKE '% tejo %'" +
            "OR LOWER(t.text) LIKE '%#dao %'" +
            "OR LOWER(t.text) LIKE '%#dão %'" +
            "OR LOWER(t.text) LIKE '%#tejo %'" +
            "OR LOWER(t.text) LIKE '%alcobaça%'" +
            "OR LOWER(t.text) LIKE '%alenquer%'" +
            "OR LOWER(t.text) LIKE '%alentejano%'" +
            "OR LOWER(t.text) LIKE '%alentejo%'" +
            "OR LOWER(t.text) LIKE '%algarve%'" +
            "OR LOWER(t.text) LIKE '%almeirim%'" +
            "OR LOWER(t.text) LIKE '%arruda%'" +
            "OR LOWER(t.text) LIKE '%arrábida%'" +
            "OR LOWER(t.text) LIKE '%azores%'" +
            "OR LOWER(t.text) LIKE '%açores%'" +
            "OR LOWER(t.text) LIKE '%bairrada%'" +
            "OR LOWER(t.text) LIKE '%beira interior%'" +
            "OR LOWER(t.text) LIKE '%beiras %'" +
            "OR LOWER(t.text) LIKE '%borba%'" +
            "OR LOWER(t.text) LIKE '%bucelas%'" +
            "OR LOWER(t.text) LIKE '%carcavelos%'" +
            "OR LOWER(t.text) LIKE '%cartaxo%'" +
            "OR LOWER(t.text) LIKE '%castelo rodrigo%'" +
            "OR LOWER(t.text) LIKE '%chamusca%'" +
            "OR LOWER(t.text) LIKE '%chaves%'" +
            "OR LOWER(t.text) LIKE '%colares%'" +
            "OR LOWER(t.text) LIKE '%coruche%'" +
            "OR LOWER(t.text) LIKE '%cova da beira%'" +
            "OR LOWER(t.text) LIKE '%do tejo%'" +
            "OR LOWER(t.text) LIKE '%encostas de aire%'" +
            "OR LOWER(t.text) LIKE '%evora%'" +
            "OR LOWER(t.text) LIKE '%granja-amareleja%'" +
            "OR LOWER(t.text) LIKE '%green wine%'" +
            "OR LOWER(t.text) LIKE '%lisboa%'" +
            "OR LOWER(t.text) LIKE '%lourinha%'" +
            "OR LOWER(t.text) LIKE '%lourinhã%'" +
            "OR LOWER(t.text) LIKE '%madeira%'" +
            "OR LOWER(t.text) LIKE '%madeirense%'" +
            "OR LOWER(t.text) LIKE '%minho%'" +
            "OR LOWER(t.text) LIKE '%obidos%'" +
            "OR LOWER(t.text) LIKE '%palmela%'" +
            "OR LOWER(t.text) LIKE '%pinhel%'" +
            "OR LOWER(t.text) LIKE '%planalto mirandês%'" +
            "OR LOWER(t.text) LIKE '%portimao%'" +
            "OR LOWER(t.text) LIKE '%portimão%'" +
            "OR LOWER(t.text) LIKE '%reguengos%'" +
            "OR LOWER(t.text) LIKE '%santarém%'" +
            "OR LOWER(t.text) LIKE '%setubal%'" +
            "OR LOWER(t.text) LIKE '%setúbal%'" +
            "OR LOWER(t.text) LIKE '%tavira%'" +
            "OR LOWER(t.text) LIKE '%torres vedras%'" +
            "OR LOWER(t.text) LIKE '%transmontano%'" +
            "OR LOWER(t.text) LIKE '%trás-os-montes%'" +
            "OR LOWER(t.text) LIKE '%távora-varosa%'" +
            "OR LOWER(t.text) LIKE '%valpaços%'" +
            "OR LOWER(t.text) LIKE '%varosa%'" +
            "OR LOWER(t.text) LIKE '%vidigueira%'" +
            "OR LOWER(t.text) LIKE '%vinho verde%'" +
            "OR LOWER(t.text) LIKE '%óbidos%')" +
            "AND t.lang like 'en'")
    Set<TweetEntity> findAllEnglishOthers();

    @Query("SELECT t FROM TweetEntity t WHERE (LOWER(t.text) like '% port %'" +
            "OR LOWER(t.text) like '%#port %'" +
            "OR LOWER(t.text) like '%porto%')" +
            "AND (LOWER(t.text) like '%tast%'" +
            "OR LOWER(t.text) like '%smell%'" +
            "OR LOWER(t.text) like '%winelover%'" +
            "OR LOWER(t.text) like '%tawny%'" +
            "OR LOWER(t.text) like '%ruby%'" +
            "OR LOWER(t.text) like '%rose%'" +
            "OR LOWER(t.text) like '%white%'" +
            "OR LOWER(t.text) like '%degustat%'" +
            "OR LOWER(t.text) like '%flav%')")
    Set<TweetEntity> findAllPortoDoc();

    @Query("SELECT t FROM TweetEntity t WHERE (LOWER(t.text) like '%douro%')" +
            "AND (LOWER(t.text) like '%tast%'" +
            "OR LOWER(t.text) like '%smell%'" +
            "OR LOWER(t.text) like '%winelover%'" +
            "OR LOWER(t.text) like '%tawny%'" +
            "OR LOWER(t.text) like '%ruby%'" +
            "OR LOWER(t.text) like '%rose%'" +
            "OR LOWER(t.text) like '%white%'" +
            "OR LOWER(t.text) like '%degustat%'" +
            "OR LOWER(t.text) like '%flav%')")
    Set<TweetEntity> findAllDouroDoc();

    @Query("SELECT t FROM TweetEntity t WHERE (" +
            "LOWER(t.text) LIKE '% beiras %'" +
            "OR LOWER(t.text) LIKE '% dao %'" +
            "OR LOWER(t.text) LIKE '% dão %'" +
            "OR LOWER(t.text) LIKE '% moura %'" +
            "OR LOWER(t.text) LIKE '% tejo %'" +
            "OR LOWER(t.text) LIKE '%#dao %'" +
            "OR LOWER(t.text) LIKE '%#dão %'" +
            "OR LOWER(t.text) LIKE '%#tejo %'" +
            "OR LOWER(t.text) LIKE '%alcobaça%'" +
            "OR LOWER(t.text) LIKE '%alenquer%'" +
            "OR LOWER(t.text) LIKE '%alentejano%'" +
            "OR LOWER(t.text) LIKE '%alentejo%'" +
            "OR LOWER(t.text) LIKE '%algarve%'" +
            "OR LOWER(t.text) LIKE '%almeirim%'" +
            "OR LOWER(t.text) LIKE '%arruda%'" +
            "OR LOWER(t.text) LIKE '%arrábida%'" +
            "OR LOWER(t.text) LIKE '%azores%'" +
            "OR LOWER(t.text) LIKE '%açores%'" +
            "OR LOWER(t.text) LIKE '%bairrada%'" +
            "OR LOWER(t.text) LIKE '%beira interior%'" +
            "OR LOWER(t.text) LIKE '%beiras %'" +
            "OR LOWER(t.text) LIKE '%borba%'" +
            "OR LOWER(t.text) LIKE '%bucelas%'" +
            "OR LOWER(t.text) LIKE '%carcavelos%'" +
            "OR LOWER(t.text) LIKE '%cartaxo%'" +
            "OR LOWER(t.text) LIKE '%castelo rodrigo%'" +
            "OR LOWER(t.text) LIKE '%chamusca%'" +
            "OR LOWER(t.text) LIKE '%chaves%'" +
            "OR LOWER(t.text) LIKE '%colares%'" +
            "OR LOWER(t.text) LIKE '%coruche%'" +
            "OR LOWER(t.text) LIKE '%cova da beira%'" +
            "OR LOWER(t.text) LIKE '%do tejo%'" +
            "OR LOWER(t.text) LIKE '%encostas de aire%'" +
            "OR LOWER(t.text) LIKE '%evora%'" +
            "OR LOWER(t.text) LIKE '%granja-amareleja%'" +
            "OR LOWER(t.text) LIKE '%green wine%'" +
            "OR LOWER(t.text) LIKE '%lisboa%'" +
            "OR LOWER(t.text) LIKE '%lourinha%'" +
            "OR LOWER(t.text) LIKE '%lourinhã%'" +
            "OR LOWER(t.text) LIKE '%madeira%'" +
            "OR LOWER(t.text) LIKE '%madeirense%'" +
            "OR LOWER(t.text) LIKE '%minho%'" +
            "OR LOWER(t.text) LIKE '%obidos%'" +
            "OR LOWER(t.text) LIKE '%palmela%'" +
            "OR LOWER(t.text) LIKE '%pinhel%'" +
            "OR LOWER(t.text) LIKE '%planalto mirandês%'" +
            "OR LOWER(t.text) LIKE '%portimao%'" +
            "OR LOWER(t.text) LIKE '%portimão%'" +
            "OR LOWER(t.text) LIKE '%reguengos%'" +
            "OR LOWER(t.text) LIKE '%santarém%'" +
            "OR LOWER(t.text) LIKE '%setubal%'" +
            "OR LOWER(t.text) LIKE '%setúbal%'" +
            "OR LOWER(t.text) LIKE '%tavira%'" +
            "OR LOWER(t.text) LIKE '%torres vedras%'" +
            "OR LOWER(t.text) LIKE '%transmontano%'" +
            "OR LOWER(t.text) LIKE '%trás-os-montes%'" +
            "OR LOWER(t.text) LIKE '%távora-varosa%'" +
            "OR LOWER(t.text) LIKE '%valpaços%'" +
            "OR LOWER(t.text) LIKE '%varosa%'" +
            "OR LOWER(t.text) LIKE '%vidigueira%'" +
            "OR LOWER(t.text) LIKE '%vinho verde%'" +
            "OR LOWER(t.text) LIKE '%óbidos%')" +
            "AND (LOWER(t.text) like '%tast%'" +
            "OR LOWER(t.text) like '%smell%'" +
            "OR LOWER(t.text) like '%winelover%'" +
            "OR LOWER(t.text) like '%tawny%'" +
            "OR LOWER(t.text) like '%ruby%'" +
            "OR LOWER(t.text) like '%rose%'" +
            "OR LOWER(t.text) like '%white%'" +
            "OR LOWER(t.text) like '%degustat%'" +
            "OR LOWER(t.text) like '%flav%')")
    Set<TweetEntity> findAllOthersDoc();

    @Query("SELECT t FROM TweetEntity t WHERE (LOWER(t.text) like '% port %'" +
            "OR LOWER(t.text) like '%#port %'" +
            "OR LOWER(t.text) like '%porto%')" +
            "AND (LOWER(t.text) like '%tour%'" +
            "OR LOWER(t.text) like '%smell%'" +
            "OR LOWER(t.text) like '%travel%'" +
            "OR LOWER(t.text) like '%city%'" +
            "OR LOWER(t.text) like '%region%'" +
            "OR LOWER(t.text) like '%road%'" +
            "OR LOWER(t.text) like '%harvest%'" +
            "OR LOWER(t.text) like '%trip%'" +
            "OR LOWER(t.text) like '%visit%'" +
            "OR LOWER(t.text) like '%culture%')")
    Set<TweetEntity> findAllPortoTourism();

    @Query("SELECT t FROM TweetEntity t WHERE (LOWER(t.text) like '%douro%')" +
            "AND (LOWER(t.text) like '%tour%'" +
            "OR LOWER(t.text) like '%smell%'" +
            "OR LOWER(t.text) like '%travel%'" +
            "OR LOWER(t.text) like '%city%'" +
            "OR LOWER(t.text) like '%region%'" +
            "OR LOWER(t.text) like '%road%'" +
            "OR LOWER(t.text) like '%harvest%'" +
            "OR LOWER(t.text) like '%trip%'" +
            "OR LOWER(t.text) like '%visit%'" +
            "OR LOWER(t.text) like '%culture%')")
    Set<TweetEntity> findAllDouroTourism();

    @Query("SELECT t FROM TweetEntity t WHERE (" +
            "LOWER(t.text) LIKE '% beiras %'" +
            "OR LOWER(t.text) LIKE '% dao %'" +
            "OR LOWER(t.text) LIKE '% dão %'" +
            "OR LOWER(t.text) LIKE '% moura %'" +
            "OR LOWER(t.text) LIKE '% tejo %'" +
            "OR LOWER(t.text) LIKE '%#dao %'" +
            "OR LOWER(t.text) LIKE '%#dão %'" +
            "OR LOWER(t.text) LIKE '%#tejo %'" +
            "OR LOWER(t.text) LIKE '%alcobaça%'" +
            "OR LOWER(t.text) LIKE '%alenquer%'" +
            "OR LOWER(t.text) LIKE '%alentejano%'" +
            "OR LOWER(t.text) LIKE '%alentejo%'" +
            "OR LOWER(t.text) LIKE '%algarve%'" +
            "OR LOWER(t.text) LIKE '%almeirim%'" +
            "OR LOWER(t.text) LIKE '%arruda%'" +
            "OR LOWER(t.text) LIKE '%arrábida%'" +
            "OR LOWER(t.text) LIKE '%azores%'" +
            "OR LOWER(t.text) LIKE '%açores%'" +
            "OR LOWER(t.text) LIKE '%bairrada%'" +
            "OR LOWER(t.text) LIKE '%beira interior%'" +
            "OR LOWER(t.text) LIKE '%beiras %'" +
            "OR LOWER(t.text) LIKE '%borba%'" +
            "OR LOWER(t.text) LIKE '%bucelas%'" +
            "OR LOWER(t.text) LIKE '%carcavelos%'" +
            "OR LOWER(t.text) LIKE '%cartaxo%'" +
            "OR LOWER(t.text) LIKE '%castelo rodrigo%'" +
            "OR LOWER(t.text) LIKE '%chamusca%'" +
            "OR LOWER(t.text) LIKE '%chaves%'" +
            "OR LOWER(t.text) LIKE '%colares%'" +
            "OR LOWER(t.text) LIKE '%coruche%'" +
            "OR LOWER(t.text) LIKE '%cova da beira%'" +
            "OR LOWER(t.text) LIKE '%do tejo%'" +
            "OR LOWER(t.text) LIKE '%encostas de aire%'" +
            "OR LOWER(t.text) LIKE '%evora%'" +
            "OR LOWER(t.text) LIKE '%granja-amareleja%'" +
            "OR LOWER(t.text) LIKE '%green wine%'" +
            "OR LOWER(t.text) LIKE '%lisboa%'" +
            "OR LOWER(t.text) LIKE '%lourinha%'" +
            "OR LOWER(t.text) LIKE '%lourinhã%'" +
            "OR LOWER(t.text) LIKE '%madeira%'" +
            "OR LOWER(t.text) LIKE '%madeirense%'" +
            "OR LOWER(t.text) LIKE '%minho%'" +
            "OR LOWER(t.text) LIKE '%obidos%'" +
            "OR LOWER(t.text) LIKE '%palmela%'" +
            "OR LOWER(t.text) LIKE '%pinhel%'" +
            "OR LOWER(t.text) LIKE '%planalto mirandês%'" +
            "OR LOWER(t.text) LIKE '%portimao%'" +
            "OR LOWER(t.text) LIKE '%portimão%'" +
            "OR LOWER(t.text) LIKE '%reguengos%'" +
            "OR LOWER(t.text) LIKE '%santarém%'" +
            "OR LOWER(t.text) LIKE '%setubal%'" +
            "OR LOWER(t.text) LIKE '%setúbal%'" +
            "OR LOWER(t.text) LIKE '%tavira%'" +
            "OR LOWER(t.text) LIKE '%torres vedras%'" +
            "OR LOWER(t.text) LIKE '%transmontano%'" +
            "OR LOWER(t.text) LIKE '%trás-os-montes%'" +
            "OR LOWER(t.text) LIKE '%távora-varosa%'" +
            "OR LOWER(t.text) LIKE '%valpaços%'" +
            "OR LOWER(t.text) LIKE '%varosa%'" +
            "OR LOWER(t.text) LIKE '%vidigueira%'" +
            "OR LOWER(t.text) LIKE '%vinho verde%'" +
            "OR LOWER(t.text) LIKE '%óbidos%')" +
            "AND (LOWER(t.text) like '%tour%'" +
            "OR LOWER(t.text) like '%smell%'" +
            "OR LOWER(t.text) like '%travel%'" +
            "OR LOWER(t.text) like '%city%'" +
            "OR LOWER(t.text) like '%region%'" +
            "OR LOWER(t.text) like '%road%'" +
            "OR LOWER(t.text) like '%harvest%'" +
            "OR LOWER(t.text) like '%trip%'" +
            "OR LOWER(t.text) like '%visit%'" +
            "OR LOWER(t.text) like '%culture%')")
    Set<TweetEntity> findAllOthersTourism();
}

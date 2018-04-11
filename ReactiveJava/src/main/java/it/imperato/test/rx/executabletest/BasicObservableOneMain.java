package it.imperato.test.rx.executabletest;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import it.imperato.test.rx.utils.City;
import it.imperato.test.rx.utils.TUtilities;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.List;

public class BasicObservableOneMain {

    private static Logger log  = LogManager.getLogger(BasicObservableOneMain.class);

    private static String result = "N.D.";

    public static void main(String[] args) {

        // Observable create
        Observable<City> cityObservable = Observable.create(emitter -> {
            try {
                List<City> cityList = TUtilities.getAllCityList();
                for (City city : cityList) {
                    emitter.onNext(city);
                }
                emitter.onComplete();
            } catch (Exception e) {
                emitter.onError(e);
            }
        });

        // Simple consumer with delay:
        Consumer<Object> cityConsumer = c -> {
            Thread.sleep(1000);
            log.info(c.toString());
        };

        // CompositeDisposable and 2 disposable, disposable1 has cityConsumer subscription:
        CompositeDisposable compositeDisposable = new CompositeDisposable();
        Disposable disposable1 = cityObservable.subscribe(cityConsumer);
        Disposable disposable2 = cityObservable.subscribe(c -> log.info("subscribed: "+c));
        // ridefinisco il disposable2, aggiungendo l'handler per il caso di errore:
        disposable2 = cityObservable.subscribe(
                c -> log.info("subscribed: "+c), e -> log.error(e.getMessage()));
        compositeDisposable.add(disposable1);
        compositeDisposable.add(disposable2);
        if (!compositeDisposable.isDisposed()){
            log.info("CompositeDisposable non ancora disposed: " +
                    "eseguo il dispose della subscription quando non più interessato a emitted data");
            compositeDisposable.dispose();
        } else {
            log.info("CompositeDisposable già disposed");
        }

        // ## Simple subscription a un valore fissato:
        Observable<String> observer = Observable.just("Hello world!");
        observer.subscribe(s -> {result=s; log.info("callable res: "+result);}); // Callable as subscriber
        log.info("res: "+result);

        // ## City list subscribtion con Observer:
        // rxjava 1: Subscriber<City> mySubscriber = new Subscriber<City>() {
        Observer<City> myObserver = new Observer<City>() {
            @Override
            public void onSubscribe(Disposable disposable) {
                log.info("[myObserver] onSubscribe: "+disposable);
            }
            @Override
            public void onNext(City s) {
                log.info("[myObserver] onNext: "+s);
            }
            @Override
            public void onError(Throwable throwable) {
                log.error("[myObserver] onError");
            }
            @Override
            public void onComplete() {
                log.info("[myObserver] onComplete");
            }
        };

        cityObservable.subscribe(myObserver);

        log.info("##### onlyCityNameList test");
        List<String> onlyCityNameList = Arrays.asList("Roma","Firenze","Verona");
        // Ogni oggetto prima va al map e poi eventualmente alla gestione relativa al subscrive:
        Observable<String> observerOnlyCityNameList = Observable.fromArray(onlyCityNameList) // fromArray, just
                .flatMapIterable(lst -> lst);
        observerOnlyCityNameList.map(w -> new City(w)).subscribe(myObserver);

    }

}

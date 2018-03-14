package it.imperato.test.rx.observable;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import it.imperato.test.rx.utils.City;
import it.imperato.test.rx.utils.TUtilities;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import io.reactivex.Observable;

import java.util.List;

public class BasicObservble {

    private static Logger log  = LogManager.getLogger(BasicObservble.class);

    public static void main(String[] args) {

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

        CompositeDisposable compositeDisposable = new CompositeDisposable();

        Consumer<Object> cityConsumer = c -> {
            Thread.sleep(1000);
            log.info(c.toString());
        };

        Disposable disposable2 = cityObservable.subscribe(c -> log.info("subscribed: "+c));
        Disposable disposable1 = cityObservable.subscribe(cityConsumer);
        compositeDisposable.add(disposable1);
        compositeDisposable.add(disposable2);
        if (!compositeDisposable.isDisposed()){
            log.info("CompositeDisposable is not disposed");
            compositeDisposable.dispose();
        } else {
            log.info("CompositeDisposable is disposed");
        }
    }

}

package it.imperato.test.rx.observable;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.subjects.BehaviorSubject;
import it.imperato.test.rx.MyModel;
import it.imperato.test.rx.utils.City;
import it.imperato.test.rx.utils.TUtilities;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class BasicObservable {

    private static Logger log  = LogManager.getLogger(BasicObservable.class);

    private static String result = "N.D.";

    private static final BehaviorSubject<Integer> subject = BehaviorSubject.create();
    private static Integer value=0;

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
        compositeDisposable.add(disposable1);
        compositeDisposable.add(disposable2);
        if (!compositeDisposable.isDisposed()){
            log.info("CompositeDisposable is not disposed");
            compositeDisposable.dispose();
        } else {
            log.info("CompositeDisposable is disposed");
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

        // Detecting variable change:
        MyModel myModel = new MyModel();
        myModel.getModelChanges()
                .subscribe(var -> log.warn("Attention, field variable changed: "+var));

        // il metodo set dell'observable avvisa del change gli observer
        myModel.setField1("1");
        myModel.setField1("2");
        myModel.setField1("3");

        // Detecting variable change - 2
        getUiElementAsObservable()
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable disposable) {
                        log.info("[Observer-detect-2] onSubscribe ");
                    }
                    @Override
                    public void onNext(Integer integer) {
                        log.info("[Observer-detect-2] onNext: "+integer);
                    }
                    @Override
                    public void onError(Throwable throwable) {
                    }
                    @Override
                    public void onComplete() {
                    }
                });
        // update di un elemento (ui element ad es.): il metodo contiene l'onNext sugli observable, su un BehaviorSubject in questo caso
        updateUiElementValue(23);
    }

    public static Observable<Integer> getUiElementAsObservable() {
        return subject;
    }

    public static void updateUiElementValue(final Integer valueAdded) {
        try {
            Thread.sleep(1000);
            synchronized (value) {
                if (value + valueAdded < 0)
                    return;
                value += valueAdded;
                subject.onNext(value);
            }
        } catch (InterruptedException e) {
            log.error("ERR",e);
        }
    }

}

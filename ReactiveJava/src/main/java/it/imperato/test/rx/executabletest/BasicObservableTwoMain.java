package it.imperato.test.rx.executabletest;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.BehaviorSubject;
import it.imperato.test.rx.model.MyModel;
import it.imperato.test.rx.utils.City;
import it.imperato.test.rx.utils.TUtilities;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class BasicObservableTwoMain {

    private static Logger log  = LogManager.getLogger(BasicObservableTwoMain.class);

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

        // Detecting variable change:
        MyModel myModel = new MyModel();
        myModel.getModelChanges()
                .subscribe(var -> log.warn("Attention, field variable changed: "+var));

        // il metodo set dell'executabletest avvisa del change gli observer
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
        // update di un elemento (ui element ad es.): il metodo contiene l'onNext sugli executabletest, su un BehaviorSubject in questo caso
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

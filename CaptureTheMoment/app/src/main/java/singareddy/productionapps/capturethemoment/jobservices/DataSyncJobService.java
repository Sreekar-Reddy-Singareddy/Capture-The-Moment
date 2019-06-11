package singareddy.productionapps.capturethemoment.jobservices;

import android.app.job.JobParameters;
import android.app.job.JobService;

public class DataSyncJobService extends JobService {

    @Override
    public boolean onStartJob(JobParameters params) {
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return false;
    }
}

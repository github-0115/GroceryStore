using System;
using System.Diagnostics;
using System.Threading;
using System.Threading.Tasks;
using System.Windows.Input;

namespace Test.Utils
{
    public class TimerProcess
    {
        private const long TimerInterval = 3000;

        private static object _locker = new object();
        private static SemaphoreSlim semaphoreSlim = new SemaphoreSlim(1, 1);
        private static Timer _timer;

        public void Start()
        {
            //_timer = new Timer(callback: Callback, state: null, dueTime: 0, period: TimerInterval);
            _timer = new Timer(async (object state) => { await Callback(state); }, state: null, dueTime: 0, period: TimerInterval);
        }

        public void Stop()
        {
            _timer.Dispose();
        }

        public async Task Callback(object state)
        {

            await semaphoreSlim.WaitAsync();
            try
            {
                _timer.Change(Timeout.Infinite, Timeout.Infinite);

                Debug.WriteLine($"task started...{DateTime.Now.ToString()}");
                await Task.Delay(5000);
                Debug.WriteLine($"task ended...{DateTime.Now.ToString()}");

            }
            finally
            {
                semaphoreSlim.Release();
                _timer.Change(TimerInterval, TimerInterval);
            }

        }

        //public void Callback(object state)
        //{
        //    var hasLock = false;

        //    try
        //    {
        //        Monitor.TryEnter(_locker, ref hasLock);
        //        if (!hasLock)
        //        {
        //            return;
        //        }
        //        _timer.Change(Timeout.Infinite, Timeout.Infinite);
        //Debug.WriteLine($"task started...{DateTime.Now.ToString()}");
        //        await Task.Delay(5000);
        //Debug.WriteLine($"task ended...{DateTime.Now.ToString()}");
        //        // Do something
        //    }
        //    finally
        //    {
        //        if (hasLock)
        //        {
        //            Monitor.Exit(_locker);
        //            _timer.Change(TimerInterval, TimerInterval);
        //        }
        //    }
        //}
    }
}
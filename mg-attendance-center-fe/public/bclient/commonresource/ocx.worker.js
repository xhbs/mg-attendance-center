var threadInfoStruct=0;var selfThreadId=0;var parentThreadId=0;var tempDoublePtr=0;var STACK_BASE=0;var STACKTOP=0;var STACK_MAX=0;var buffer;var DYNAMICTOP_PTR=0;var DYNAMIC_BASE=0;var noExitRuntime;var PthreadWorkerInit={};var __performance_now_clock_drift=0;var Module={};function assert(condition,text){if(!condition)abort("Assertion failed: "+text)}this.addEventListener("error",function(e){if(e.message.indexOf("SimulateInfiniteLoop")!=-1)return e.preventDefault();var errorSource=" in "+e.filename+":"+e.lineno+":"+e.colno;console.error("Pthread "+selfThreadId+" uncaught exception"+(e.filename||e.lineno||e.colno?errorSource:"")+": "+e.message+". Error object:");console.error(e.error)});function threadPrintErr(){var text=Array.prototype.slice.call(arguments).join(" ");console.error(text);console.error((new Error()).stack)}function threadAlert(){var text=Array.prototype.slice.call(arguments).join(" ");postMessage({cmd:"alert",text:text,threadId:selfThreadId})}var err=threadPrintErr;this.alert=threadAlert;function resetPrototype(constructor,attrs){var object=Object.create(constructor.prototype);for(var key in attrs){if(attrs.hasOwnProperty(key)){object[key]=attrs[key]}}return object}Module["instantiateWasm"]=function(info,receiveInstance){var instance=new WebAssembly.Instance(wasmModule,info);wasmModule=null;receiveInstance(instance);return instance.exports};var wasmModule;var wasmMemory;this.onmessage=function(e){try{if(e.data.cmd==="load"){tempDoublePtr=e.data.tempDoublePtr;DYNAMIC_BASE=e.data.DYNAMIC_BASE;DYNAMICTOP_PTR=e.data.DYNAMICTOP_PTR;Module["STACK_MAX"]=Module["STACKTOP"]=2147483647;wasmModule=e.data.wasmModule;wasmMemory=e.data.wasmMemory;buffer=wasmMemory.buffer;PthreadWorkerInit=e.data.PthreadWorkerInit;Module["ENVIRONMENT_IS_PTHREAD"]=true;if(typeof e.data.urlOrBlob==="string"){importScripts(e.data.urlOrBlob)}else{var objectUrl=URL.createObjectURL(e.data.urlOrBlob);importScripts(objectUrl);URL.revokeObjectURL(objectUrl)}if(typeof FS!=="undefined"&&typeof FS.createStandardStreams==="function")FS.createStandardStreams();postMessage({cmd:"loaded"})}else if(e.data.cmd==="objectTransfer"){PThread.receiveObjectTransfer(e.data)}else if(e.data.cmd==="run"){__performance_now_clock_drift=performance.now()-e.data.time;threadInfoStruct=e.data.threadInfoStruct;__register_pthread_ptr(threadInfoStruct,0,0);selfThreadId=e.data.selfThreadId;parentThreadId=e.data.parentThreadId;var max=e.data.stackBase+e.data.stackSize;var top=e.data.stackBase;STACK_BASE=top;STACKTOP=top;STACK_MAX=max;assert(threadInfoStruct);assert(selfThreadId);assert(parentThreadId);assert(STACK_BASE!=0);assert(max>e.data.stackBase);assert(max>top);assert(e.data.stackBase===top);Module["establishStackSpace"](e.data.stackBase,e.data.stackBase+e.data.stackSize);writeStackCookie();PThread.receiveObjectTransfer(e.data);PThread.setThreadStatus(_pthread_self(),1);try{var result=Module["dynCall_ii"](e.data.start_routine,e.data.arg);checkStackCookie()}catch(e){if(e==="Canceled!"){PThread.threadCancel();return}else if(e==="SimulateInfiniteLoop"||e==="pthread_exit"){return}else{Atomics.store(HEAPU32,threadInfoStruct+4>>2,e instanceof ExitStatus?e.status:-2);Atomics.store(HEAPU32,threadInfoStruct+0>>2,1);if(typeof _emscripten_futex_wake!=="function"){err("Thread Initialisation failed.");throw e}_emscripten_futex_wake(threadInfoStruct+0,2147483647);if(!(e instanceof ExitStatus))throw e}}if(!noExitRuntime)PThread.threadExit(result)}else if(e.data.cmd==="cancel"){if(threadInfoStruct&&PThread.thisThreadCancelState==0){PThread.threadCancel()}}else if(e.data.target==="setimmediate"){}else if(e.data.cmd==="processThreadQueue"){if(threadInfoStruct){_emscripten_current_thread_process_queued_calls()}}else{err("worker.js received unknown command "+e.data.cmd);console.error(e.data)}}catch(e){console.error("worker.js onmessage() captured an uncaught exception: "+e);console.error(e.stack);throw e}};
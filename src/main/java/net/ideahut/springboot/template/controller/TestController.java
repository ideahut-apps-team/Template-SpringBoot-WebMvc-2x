package net.ideahut.springboot.template.controller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.function.Supplier;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;
import net.ideahut.springboot.annotation.Public;
import net.ideahut.springboot.entity.EntityTrxManager;
import net.ideahut.springboot.entity.replica.ReplicaInfo;
import net.ideahut.springboot.helper.ErrorHelper;
import net.ideahut.springboot.helper.StringHelper;
import net.ideahut.springboot.helper.ThreadHelper;
import net.ideahut.springboot.helper.WebMvcHelper;
import net.ideahut.springboot.object.Message;
import net.ideahut.springboot.object.Result;
import net.ideahut.springboot.template.entity.Information;

@Slf4j
@Public
//@Body(response = true)
@ComponentScan
@RestController
@RequestMapping("/test")
class TestController {
	
	private EntityTrxManager entityTrxManager;
	
	@Autowired
	TestController(EntityTrxManager entityTrxManager) {
		this.entityTrxManager = entityTrxManager;
	}
	
	@GetMapping("/exp")
	void exp() {
		throw ErrorHelper.exception(() -> StringHelper.format("ERROR-{}", System.nanoTime()));
	}
	
	@GetMapping("/vt")
	Result vt() {
		Thread thread = Thread.currentThread();
		boolean isVt = ThreadHelper.isThreadVirtual(thread);
		return Result.success(isVt).setInfo("thread", thread.getName());
	}
	
	@GetMapping("/bytes")
	byte[] bytes() {
		return ("BYTES-" + System.nanoTime()).getBytes();
	}
	
	@GetMapping("/string")
	String string() {
		return "STRING-" + System.nanoTime();
	}
	
	@GetMapping("/strre")
	ResponseEntity<String> strre() {
		return ResponseEntity.ok()
		.header("Test-Strre", "string")
		.body("STRRE-" + System.nanoTime());
	}

	@GetMapping("/send")
	void send(
		HttpServletRequest request,
		HttpServletResponse response
	) {
		//WebMvcHelper.sendResponse(request, response, null, false, "SEND-" + System.nanoTime()); //-
		WebMvcHelper.sendResponse(request, response, "SEND-" + System.nanoTime()); //-
		//WebMvcHelper.sendResponse(request, response, null, false, System.nanoTime()); //-
		//WebMvcHelper.sendResponse(request, response, System.nanoTime()); //-
		//WebMvcHelper.sendResponse(request, response, new Exception("ERROR-SEND-" + System.nanoTime())); //-
		
		/**
		String hval = System.nanoTime() + "";
		response.setHeader("xxx1", hval);
		response.setHeader("xxx2", hval);
		ResponseEntity<Message> re = ResponseEntity.ok()
		.header("xxx2", "KEREN", "LAGI")
		.header("yyyy", "NONE")
		.body(Message.of("YYY", "VALUE"));
		WebMvcHelper.sendResponse(request, response, re);
		*/
	}
	
	@GetMapping("/result")
	Result result() {
		return Result.success("RESULT-" + System.nanoTime());
	}
	
	@GetMapping("/message")
	Message message() {
		return Message.of("MSG", "MESSAGE-{}", System.nanoTime());
	}
	
	@GetMapping("/replica")
	Integer replica() {
		return ReplicaInfo.getLatestReplica(entityTrxManager.getDefaultTrxManagerInfo(), Information.class);
	}
	
	@GetMapping("/outstream")
	void outstream(HttpServletResponse response) {
		ByteArrayOutputStream out = new ByteArrayOutputStream() {
			private byte[] bytes = ("Haloooo-" + System.nanoTime()).getBytes();

			@Override
			public synchronized void writeTo(OutputStream out) throws IOException {
				out.write(bytes);
			}
		};
		try {
			out.writeTo(response.getOutputStream());
		} catch (IOException e) {
			throw ErrorHelper.exception(e);
		}
	}
	
	@GetMapping("/logger")
	void logger() {
		Throwable throwable = new Exception(StringHelper.format("EXCEPTION: {}", System.nanoTime() + ""));
		log.debug("{}", message(() -> "DEBUG"), throwable);
		log.trace("{}", message(() -> "TRACE"), throwable);
		log.info("{}", message(() -> "INFO-" + System.nanoTime()), throwable);
		log.warn("{}", message(() -> "WARN"), throwable);
		log.error("{}", message(() -> "ERROR"), throwable);
	}
	
	private Object message(Supplier<CharSequence> message) {
		return new Object() {
			@Override
			public String toString() {
				System.out.println("CALLED");
				CharSequence charSequence = message != null ? message.get() : null;
				return charSequence != null ? charSequence.toString() : "";
			}
		};
	}
}

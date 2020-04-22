package wooteco.chess.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import wooteco.chess.domain.position.Position;
import wooteco.chess.service.GameManagerService;
import wooteco.chess.util.WebOutputRenderer;

@Controller
public class ChessController {
	private GameManagerService gameManagerService;

	public ChessController(GameManagerService gameManagerService) {
		this.gameManagerService = gameManagerService;
	}

	@GetMapping("/start")
	public String start(Model model) {
		gameManagerService.resetGame();
		model.addAttribute("piecesDto", WebOutputRenderer.toPiecesDto(gameManagerService.getBoard()));
		model.addAttribute("turn", gameManagerService.getCurrentTurn().name());
		return "board";
	}

	@GetMapping("/resume")
	public String resume(Model model) {
		model.addAttribute("piecesDto", WebOutputRenderer.toPiecesDto(gameManagerService.getBoard()));
		model.addAttribute("turn", gameManagerService.getCurrentTurn().name());

		return "board";
	}

	@PostMapping("/move")
	public String move(Model model, @RequestParam(defaultValue = "") String target,
		@RequestParam(defaultValue = "") String destination) {
		try {
			gameManagerService.move(Position.of(target),
				Position.of(destination));
		} catch (RuntimeException e) {
			model.addAttribute("error", e.getMessage());
		}
		model.addAttribute("piecesDto", WebOutputRenderer.toPiecesDto(gameManagerService.getBoard()));
		model.addAttribute("turn", gameManagerService.getCurrentTurn().name());

		if (!gameManagerService.isKingAlive()) {
			model.addAttribute("winner", gameManagerService.getCurrentTurn().reverse());
			gameManagerService.resetGame();
			return "end";
		}
		return "board";
	}

	@GetMapping("/status")
	public String status(Model model) {
		model.addAttribute("piecesDto", WebOutputRenderer.toPiecesDto(gameManagerService.getBoard()));
		model.addAttribute("turn", gameManagerService.getCurrentTurn().name());
		model.addAttribute("scores", WebOutputRenderer.scoreToModel(gameManagerService.calculateEachScore()));
		return "board";
	}

	@GetMapping("/end")
	public String end() {
		gameManagerService.resetGame();
		return "end";
	}
}

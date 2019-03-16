package com.raphydaphy.arcanemagic.notebook;

import com.raphydaphy.arcanemagic.ArcaneMagic;
import com.raphydaphy.arcanemagic.api.docs.INotebookElement;
import com.raphydaphy.arcanemagic.api.docs.INotebookSection;
import com.raphydaphy.arcanemagic.init.ArcaneMagicConstants;
import com.raphydaphy.arcanemagic.util.DataHolder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public class InfusionNotebookSection implements INotebookSection
{
	@Override
	public Identifier getID()
	{
		return new Identifier(ArcaneMagic.DOMAIN, "infusion");
	}

	@Override
	public boolean isVisibleTo(DataHolder player)
	{
		return player.getAdditionalData().getBoolean(ArcaneMagicConstants.ANALYZED_ENCHANTING_TABLE_KEY);
	}

	@Override
	public List<INotebookElement> getElements(DataHolder player, int page)
	{
		List<INotebookElement> elements = new ArrayList<>();

		if (page == 0)
		{
			elements.add(new NotebookElement.SmallHeading("notebook.arcanemagic.infusion.title").withPadding(3));
		} else
		{
			elements.add(new NotebookElement.Padding(3));
		}

		int firstText = NotebookElement.textPages("notebook.arcanemagic.infusion.0", 2);
		elements.addAll(NotebookElement.wrapText("notebook.arcanemagic.infusion.0", 2, 0, page));

		if (page == firstText + 1)
		{
			elements.add(new NotebookElement.Padding(4));
			elements.add(new NotebookElement.Paragraph(true, 1,"block.arcanemagic.crystal_infuser").withPadding(10));
			elements.add(new NotebookElement.Recipe( MinecraftClient.getInstance().world.getRecipeManager().get(new Identifier(ArcaneMagic.DOMAIN, "crystal_infuser")).orElse(null)));
		}

		if (page >= firstText + 2 && player.getAdditionalData().getBoolean(ArcaneMagicConstants.PLACED_INFUSER_KEY))
		{
			elements.addAll(NotebookElement.wrapText("notebook.arcanemagic.infusion.1", 0, firstText + 2, page));
		}
		return elements;
	}

	@Override
	public int getPageCount(DataHolder player)
	{
		return NotebookElement.textPages("notebook.arcanemagic.infusion.0", 2) + (player.getAdditionalData().getBoolean(ArcaneMagicConstants.PLACED_INFUSER_KEY) ? NotebookElement.textPages("notebook.arcanemagic.infusion.1", 0) + 2 : 1);
	}
}